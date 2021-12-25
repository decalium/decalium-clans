package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.api.ClanCreationResult;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.editor.SqlClanEditor;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanBuilderMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanHomeMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.MemberMapper;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SqlClanStorage implements ClanStorage {

    @Language("SQL")
    private static final String SELECT_CLANS = """
            SELECT C.id clan_id, C.tag clan_tag, C.owner, clan_owner, C.display_name clan_display_name,
            M.uuid member_uuid, M.role member_role,
            H.name home_name, H.creator home_creator, H.display_name home_display_name, H.icon home_icon,
            H.x home_x, H.y home_y, H.z home_z, H.world home_world,
            S.type statistic_type, S.value statistic_value,
            FROM clans as C
            LEFT JOIN members as M ON C.id = M.clan_id
            LEFT JOIN homes as H ON C.id = H.clan_id
            LEFT JOIN stats as S ON C.id = S.clan_id
            """;


    private static final String SELECT_CLAN_WITH_TAG = SELECT_CLANS + " WHERE C.tag=<tag>";
    private static final String SELECT_USER_CLAN = SELECT_CLANS + " WHERE C.id=(SELECT clan_id FROM members WHERE uuid=<uuid>)";
    @Language("SQL")
    private static final String INSERT_CLAN = "INSERT OR IGNORE INTO clans(`tag`, `owner`, `display_name`) VALUES (<tag>, <owner>, <display_name>)";

    @Language("SQL")
    private static final String DELETE_CLAN = "DELETE FROM clans WHERE id=<id>";
    @Language("SQL")
    private static final String DELETE_MEMBERS = "DELETE FROM members WHERE `clan_id=`<clan_id>";
    @Language("SQL")
    private static final String DELETE_HOMES = "DELETE FROM homes WHERE `clan_id`=<clan_id>";
    @Language("SQL")
    private static final String DELETE_STATISTICS = "DELETE FROM statistics WHERE `clan_tag`=<clan_id>";

    private final Jdbi jdbi;
    private final Plugin plugin;
    private final DecaliumClansApi clansApi;


    public SqlClanStorage(@NotNull Plugin plugin, @NotNull Jdbi jdbi, @NotNull DecaliumClansApi clansApi) {
        this.plugin = plugin;
        this.jdbi = jdbi;
        this.clansApi = clansApi;
    }
    @Override
    public @Nullable Clan loadClan(@NotNull String tag) {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLAN_WITH_TAG).bind("tag", tag))
                .findFirst().orElse(null));
    }

    @Override
    public @Nullable Clan loadUserClan(@NotNull UUID uuid) {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_USER_CLAN).bind("uuid", uuid)))
                .findFirst().orElse(null);
    }

    @Override
    public @NotNull Set<Clan> loadClans() {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLANS))).collect(Collectors.toSet());
    }

    @Override
    public ClanCreationResult saveClan(@NotNull DraftClan draftClan) {
        return jdbi.inTransaction(handle -> {
            Optional<Integer> optionalId = handle.createUpdate(INSERT_CLAN)
                    .bind("tag", draftClan.getTag())
                    .bind("owner", draftClan.getOwner())
                    .bind("display_name", draftClan.getDisplayName())
                    .executeAndReturnGeneratedKeys("id").mapTo(int.class).findFirst();
            if(optionalId.isEmpty()) {
                handle.rollback();
                return ClanCreationResult.alreadyExists();
            }
            int id = optionalId.get();

            Clan clan = ClanBuilder.asBuilder(draftClan, id).build();

            PreparedBatch batch = handle.prepareBatch("INSERT OR IGNORE INTO members (clan_id, uuid, role) VALUES (<id>, <uuid>, <role>)");
            for(ClanMember member : draftClan.getMembers()) {
                batch.bind("id", id)
                        .bind("uuid", member.getUniqueId())
                        .bind("role", member.getRole().getName())
                        .add();
            }
            int updates = Arrays.stream(batch.execute()).sum(); // some members weren't added; those are already in other clans;

            if(updates != draftClan.getMembers().size()) {
                handle.rollback();
                return ClanCreationResult.membersInOtherClans();
            }





            SqlClanEditor editor = new SqlClanEditor(handle, clan);
            for(ClanMember member : draftClan.getMembers()) {
                editor.addMember(member);
            }
            for(ClanHome home : draftClan.getHomes()) {
                editor.addHome(home);
            }
            draftClan.getStatistics().forEach(editor::setStatistic);

            return ClanCreationResult.success(clan);
        });
    }

    @Override
    public void editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> editor) {
        jdbi.useTransaction(handle -> editor.accept(new SqlClanEditor(handle, clan)));
    }

    @Override
    public void removeClan(@NotNull Clan clan) {
        final int id = clan.getId();
        jdbi.useTransaction(handle -> {
            handle.createUpdate(DELETE_CLAN).bind("id", id).execute();
            handle.createUpdate(DELETE_MEMBERS).bind("id", id).execute();
            handle.createUpdate(DELETE_HOMES).bind("id", id).execute();
            handle.createUpdate(DELETE_STATISTICS).bind("id", id).execute();
        });

    }

    @Override
    public boolean clanExists(@NotNull String tag) {
        return false;
    }


    private Stream<Clan> collectClans(Query query) {
        return query.registerRowMapper(ClanBuilder.class, new ClanBuilderMapper("clan"))
                .registerRowMapper(ClanMember.class, new MemberMapper(clansApi, "member"))
                .registerRowMapper(ClanHome.class, new ClanHomeMapper(plugin.getServer(), clansApi, "home"))
                .reduceRows(new LinkedHashMap<String, ClanBuilder>(), (map, rowView) -> {
                    ClanBuilder builder = map.computeIfAbsent(
                            rowView.getColumn("clan_tag", String.class),
                            clanTag -> rowView.getRow(ClanBuilder.class));
                    if (rowView.getColumn("member_uuid", UUID.class) != null) {
                        builder.addMember(rowView.getRow(ClanMember.class));
                    }
                    if (rowView.getColumn("home_name", String.class) != null) {
                        builder.addHome(rowView.getRow(ClanHome.class));
                    }
                    StatisticType statisticType = rowView.getColumn("statistic_type", StatisticType.class);
                    if(statisticType != null) {
                        builder.statistic(statisticType, rowView.getColumn("statistic_value", int.class));
                    }
                    return map;
                }).values().stream().map(ClanBuilder::build);
    }

}
