package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.IdentifiedDraftClanImpl;
import org.gepron1x.clans.plugin.storage.StorageType;
import org.gepron1x.clans.plugin.storage.implementation.sql.edition.SqlClanEdition;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanBuilderMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanHomeBuilderMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.LocationMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.MemberMapper;
import org.gepron1x.clans.plugin.util.Optionals;
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
    private static final String SELECT_CLANS = "SELECT * FROM clans_simple";



    @Language("SQL")
    private static final String CREATE_CLANS_TABLE = """
    CREATE TABLE IF NOT EXISTS `clans` (
    `id` INTEGER NOT NULL AUTO_INCREMENT,
    `tag` VARCHAR(16) NOT NULL,
    `owner` BINARY(16) NOT NULL,
    `display_name` JSON NOT NULL,
    UNIQUE(`tag`, `owner`),
    PRIMARY KEY(`id`)
    )
    """;

    @Language("SQL")
    private static final String CREATE_MEMBERS_TABLE = """
    CREATE TABLE IF NOT EXISTS `members` (
    `clan_id` INTEGER NOT NULL,
    `uuid` BINARY(16) NOT NULL,
    `role` VARCHAR(32) NOT NULL,
    UNIQUE(`clan_id`, `uuid`),
    FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE,
    PRIMARY KEY(`uuid`)
    )
    """;

    @Language("SQL")
    private static final String CREATE_HOMES_TABLE = """
    CREATE TABLE IF NOT EXISTS `homes` (
    `id` INTEGER AUTO_INCREMENT,
    `clan_id` INTEGER NOT NULL,
    `name` VARCHAR(32) NOT NULL,
    `display_name` JSON NOT NULL,
    `creator` BINARY(16) NOT NULL,
    `icon` BLOB,
    UNIQUE (`clan_id`, `name`, `creator`),
    FOREIGN KEY (`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE,
    PRIMARY KEY(`id`)
    )
    """;

    @Language("SQL")
    private static final String CREATE_LOCATIONS_TABLE = """
    CREATE TABLE IF NOT EXISTS `locations` (
    `home_id` INTEGER NOT NULL,
    `x` INTEGER NOT NULL,
    `y` INTEGER NOT NULL,
    `z` INTEGER NOT NULL,
    `world` VARCHAR(64) NOT NULL,
    FOREIGN KEY (`home_id`) REFERENCES `homes` (`id`) ON DELETE CASCADE
    )
    """;

    @Language("SQL")
    private static final String CREATE_STATISTICS_TABLE = """
    CREATE TABLE IF NOT EXISTS `statistics` (
    `clan_id` INTEGER NOT NULL UNIQUE,
    `type` VARCHAR(16) NOT NULL UNIQUE,
    `value` INTEGER NOT NULL,
    FOREIGN KEY(`clan_id`) REFERENCES `clans` (`id`) ON DELETE CASCADE
    )
    """;

    @Language("SQL")
    private static final String CREATE_SIMPLE_CLANS_VIEW = """
    CREATE OR REPLACE VIEW `clans_simple` AS
    SELECT
    `C`.`id` `clan_id`, `C`.`tag` `clan_tag`, `C`.`owner` `clan_owner`, `C`.`display_name` `clan_display_name`,
    `M`.`uuid` `member_uuid`, `M`.`role` `member_role`,
    `H`.`name` `home_name`, `H`.`creator` `home_creator`, `H`.`display_name` `home_display_name`, `H`.`icon` `home_icon`,
    `L`.`x` `location_x`, `L`.`y` `location_y`, `L`.`z` `location_z`, `L`.`world` `location_world`,
    `S`.`type` `statistic_type`, `S`.`value` `statistic_value`
    FROM `clans` `C`
    LEFT JOIN `members` `M` ON `C`.`id` = `M`.`clan_id`
    LEFT JOIN `homes` `H` ON `C`.`id` = `H`.`clan_id`
    LEFT JOIN `locations` `L` ON `H`.`id` = `L`.`home_id`
    LEFT JOIN `statistics` `S` ON `C`.`id` = `S`.`clan_id`
    """;

    @Language("SQL")
    private static final String SELECT_CLAN_WITH_ID = "SELECT * FROM clans_simple WHERE`clan_id`=?";

    @Language("SQL")
    private static final String SELECT_CLAN_WITH_TAG = "SELECT * FROM clans_simple WHERE `clan_tag`=?";
    @Language("SQL")
    private static final String SELECT_USER_CLAN = "SELECT * FROM clans_simple WHERE `clan_id`=(SELECT `clan_id` FROM `members` WHERE `uuid`=?)";
    @Language("SQL")
    private static final String INSERT_CLAN = "INSERT IGNORE INTO clans(`tag`, `owner`, `display_name`) VALUES (?, ?, ?)";

    @Language("SQL")
    private static final String INSERT_MEMBER = "INSERT IGNORE INTO members(`clan_id`, `uuid`, `role`) VALUES (?, ?, ?)";

    @Language("SQL")
    private static final String DELETE_CLAN = "DELETE FROM clans WHERE id=?";

    @Language("SQL")
    private static final String SELECT_CLAN_ID_WITH_TAG = "SELECT `id` FROM clans WHERE `tag`=?";

    @Language("SQL")
    private static final String SELECT_CLAN_ID_WITH_MEMBER = "SELECT `clan_id` FROM members WHERE `uuid`=?";

    @Language("SQL")
    private static final String SELECT_IDS = "SELECT `id` FROM clans";

    private final Jdbi jdbi;
    private final StorageType type;
    private final ClanBuilderFactory builderFactory;
    private final RoleRegistry roleRegistry;
    private final Plugin plugin;


    public SqlClanStorage(@NotNull Plugin plugin,
                          @NotNull Jdbi jdbi,
                          @NotNull StorageType type,
                          @NotNull ClanBuilderFactory builderFactory,
                          @NotNull RoleRegistry roleRegistry) {
        this.plugin = plugin;
        this.jdbi = jdbi;
        this.type = type;
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
    }

    @Override
    public void initialize() {
        jdbi.useTransaction(handle -> {
            List.of(CREATE_CLANS_TABLE,
                    CREATE_MEMBERS_TABLE,
                    CREATE_HOMES_TABLE,
                    CREATE_LOCATIONS_TABLE,
                    CREATE_STATISTICS_TABLE).forEach(s -> handle.createUpdate(s).execute());

            List.of(CREATE_SIMPLE_CLANS_VIEW).forEach(s -> handle.createUpdate(s).execute());
        });

    }

    @Override
    public void shutdown() {
        jdbi.useHandle(type::disable);

    }

    @Override
    public @Nullable IdentifiedDraftClan loadClan(@NotNull String tag) {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLAN_WITH_TAG).bind(0, tag))
                .findFirst().orElse(null));
    }

    @Override
    public @Nullable IdentifiedDraftClan loadClan(int id) {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLAN_WITH_ID).bind(0, id)))
                .findFirst().orElse(null);
    }

    @Override
    public @Nullable IdentifiedDraftClan loadUserClan(@NotNull UUID uuid) {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_USER_CLAN).bind(0, uuid)))
                .findFirst().orElse(null);
    }

    @Override
    public OptionalInt lookupId(@NotNull String tag) {
        return jdbi.withHandle(handle -> Optionals.ofNullable(
                handle.createQuery(SELECT_CLAN_ID_WITH_TAG).bind(0, tag).mapTo(Integer.class)
                        .findFirst().orElse(null)
        ));
    }

    @Override
    public OptionalInt lookupId(@NotNull UUID member) {
        return jdbi.withHandle(handle -> Optionals.ofNullable(
                handle.createQuery(SELECT_CLAN_ID_WITH_MEMBER).bind(0, member).mapTo(Integer.class)
                        .findFirst().orElse(null)
        ));
    }

    @Override
    public Set<Integer> clanIds() {
        return jdbi.withHandle(handle -> handle.createQuery(SELECT_IDS).mapTo(Integer.class).collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public @NotNull Set<IdentifiedDraftClanImpl> loadClans() {
        return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLANS))).collect(Collectors.toSet());
    }

    @Override
    public SaveResult saveClan(@NotNull DraftClan draftClan) {
        return jdbi.inTransaction(handle -> {
            Optional<Integer> optionalId = handle.createUpdate(INSERT_CLAN)
                    .bind(0, draftClan.tag())
                    .bind(1, draftClan.owner().uniqueId())
                    .bind(2, draftClan.displayName())
                    .executeAndReturnGeneratedKeys("id").mapTo(Integer.class).findFirst();
            if(optionalId.isEmpty()) {
                handle.rollback();
                return SaveResult.ALREADY_EXISTS;
            }
            int id = optionalId.get();


            PreparedBatch batch = handle.prepareBatch(INSERT_MEMBER);
            for(ClanMember member : draftClan.members()) {
                batch.bind(0, id)
                        .bind(1, member.uniqueId())
                        .bind(2, member.role().name())
                        .add();
            }
            int updates = Arrays.stream(batch.execute()).sum(); // some members weren't added; those are already in other clans;

            if(updates != draftClan.members().size()) {
                handle.rollback();
                return SaveResult.MEMBERS_IN_OTHER_CLANS;
            }

            ClanEdition editor = new SqlClanEdition(handle, id);

            for(ClanHome home : draftClan.homes()) {
                editor.addHome(home);
            }
            editor.setStatistics(draftClan.statistics());

            return SaveResult.success(id);
        });
    }

    @Override
    public void applyEdition(int id, @NotNull Consumer<ClanEdition> consumer) {
        jdbi.useHandle(handle -> consumer.accept(new SqlClanEdition(handle, id)));
    }


    @Override
    public boolean removeClan(int id) {
        return jdbi.inTransaction(handle -> handle.createUpdate(DELETE_CLAN).bind(0, id).execute() != 0);
    }



    private Stream<IdentifiedDraftClanImpl> collectClans(Query query) {
        return query.registerRowMapper(DraftClan.Builder.class, new ClanBuilderMapper("clan", builderFactory))
                .registerRowMapper(ClanMember.class, new MemberMapper(builderFactory, roleRegistry, "member"))
                .registerRowMapper(ClanHome.Builder.class, new ClanHomeBuilderMapper(builderFactory, "home"))
                .registerRowMapper(Location.class, new LocationMapper(plugin.getServer(), "location"))
                .reduceRows(new LinkedHashMap<Integer, DraftClan.Builder>(), (map, rowView) -> {
                    DraftClan.Builder builder = map.computeIfAbsent(
                            rowView.getColumn("clan_id", Integer.class),
                            clanTag -> rowView.getRow(DraftClan.Builder.class));
                    UUID owner = rowView.getColumn("clan_owner", UUID.class);

                    if (rowView.getColumn("member_uuid", byte[].class) != null) {
                        ClanMember member = rowView.getRow(ClanMember.class);
                        if(member.uniqueId().equals(owner)) {
                            builder.owner(member);
                        } else {
                            builder.addMember(rowView.getRow(ClanMember.class));
                        }
                    }

                    if (rowView.getColumn("home_name", String.class) != null) {
                        builder.addHome(rowView.getRow(ClanHome.Builder.class).location(rowView.getRow(Location.class)).build());
                    }
                    String statType = rowView.getColumn("statistic_type", String.class);

                    if(statType != null) {
                        builder.statistic(new StatisticType(statType), rowView.getColumn("statistic_value", Integer.class));
                    }
                    return map;
                }).entrySet().stream().map(entry -> new IdentifiedDraftClanImpl(entry.getKey(), entry.getValue().build()));
    }

}
