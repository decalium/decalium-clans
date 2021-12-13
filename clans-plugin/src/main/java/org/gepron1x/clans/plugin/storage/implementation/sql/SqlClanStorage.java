package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.clan.ClanImpl;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.editor.SqlClanEditor;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanBuilderMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanHomeMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.MemberMapper;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.Batch;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.statement.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SqlClanStorage implements ClanStorage {

    private static final String SELECT_CLANS = """
            SELECT C.tag clan_tag, C.owner, clan_owner, C.display_name clan_display_name,
            M.uuid member_uuid, M.role member_role,
            H.name home_name, H.creator home_creator, H.display_name home_display_name, H.icon home_icon,
            H.x home_x, H.y home_y, H.z home_z, H.world home_world,
            S.type statistic_type, S.value statistic_value,
            FROM clans as C
            LEFT JOIN members as M ON C.tag = M.clan_tag
            LEFT JOIN homes as H ON C.tag = H.clan_tag
            LEFT JOIN stats as S ON C.tag = S.clan_tag
            """;

    private static final String SELECT_CLAN_WITH_TAG = SELECT_CLANS + " WHERE C.tag=<tag>";
    private static final String SELECT_USER_CLAN = SELECT_CLANS + " WHERE C.tag=(SELECT clan_tag FROM members WHERE uuid=<uuid>)";
    private static final String INSERT_CLAN = "INSERT INTO clans(tag, owner, display_name) VALUES (<tag>, <owner>, <display_name>)";
    private static final String DELETE_CLAN = "DELETE FROM clans WHERE tag=<tag>";
    private static final String DELETE_MEMBERS = "DELETE FROM members WHERE clan_tag=<tag>";
    private static final String DELETE_HOMES = "DELETE FROM homes WHERE clan_tag=<tag>";
    private static final String DELETE_STATISTICS = "DELETE FROM statistics WHERE clan_tag=<tag>";

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
    public void saveClan(@NotNull Clan clan) {
        jdbi.useTransaction(handle -> {
            handle.createUpdate(INSERT_CLAN)
                    .bind("tag", clan.getTag())
                    .bind("owner", clan.getOwner())
                    .bind("display_name", clan.getDisplayName())
                    .execute();

            SqlClanEditor editor = new SqlClanEditor(handle, clan);
            for(ClanMember member : clan.getMembers()) {
                editor.addMember(member);
            }
            for(ClanHome home : clan.getHomes()) {
                editor.addHome(home);
            }
            clan.getStatistics().forEach(editor::setStatistic);
        });
    }

    @Override
    public void editClan(@NotNull Clan clan, @NotNull Consumer<ClanEditor> editor) {
        jdbi.useTransaction(handle -> editor.accept(new SqlClanEditor(handle, clan)));
    }

    @Override
    public void removeClan(@NotNull Clan clan) {
        final String tag = clan.getTag();
        jdbi.useTransaction(handle -> {
            handle.createUpdate(DELETE_CLAN).bind("tag", tag).execute();
            handle.createUpdate(DELETE_MEMBERS).bind("tag", tag).execute();
            handle.createUpdate(DELETE_HOMES).bind("tag", tag).execute();
            handle.createUpdate(DELETE_STATISTICS).bind("tag", tag).execute();
        });

    }

    @Override
    public boolean clanExists(@NotNull String tag) {
        return false;
    }


    private Stream<Clan> collectClans(Query query) {
        return query.registerRowMapper(Clan.Builder.class, new ClanBuilderMapper(clansApi, "clan"))
                .registerRowMapper(ClanMember.class, new MemberMapper(clansApi, "member"))
                .registerRowMapper(ClanHome.class, new ClanHomeMapper(plugin.getServer(), clansApi, "home"))
                .reduceRows(new LinkedHashMap<String, Clan.Builder>(), (map, rowView) -> {
                    Clan.Builder builder = map.computeIfAbsent(
                            rowView.getColumn("clan_tag", String.class),
                            clanTag -> rowView.getRow(Clan.Builder.class));
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
                }).values().stream().map(Clan.Builder::build);
    }

}
