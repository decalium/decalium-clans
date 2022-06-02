package org.gepron1x.clans.plugin.storage.implementation.sql.edition;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.HomeEdition;
import org.gepron1x.clans.api.edition.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public final class SqlClanEdition implements ClanEdition {
    @Language("SQL")
    private static final String UPDATE_DISPLAY_NAME = "UPDATE `clans` SET `display_name`=? WHERE `id`=?";
    @Language("SQL")
    private static final String UPDATE_STATISTIC = "UPDATE `statistics` SET `value`=? WHERE `clan_id`=? AND type=?";
    @Language("SQL")
    private static final String DELETE_STATISTIC = "DELETE FROM `statistics` WHERE `clan_id`=? AND `type`=?";
    @Language("SQL")
    private static final String INSERT_MEMBER = "INSERT IGNORE INTO `members` (`clan_id`, `uuid`, `role`) VALUES (?, ?, ?)";
    @Language("SQL")
    private static final String DELETE_MEMBER = "DELETE FROM `members` WHERE `uuid`=?";
    @Language("SQL")
    private static final String INSERT_HOME = "INSERT IGNORE INTO `homes` (`clan_id`, `name`, `creator`, `display_name`, `icon`) VALUES (?, ?, ?, ?, ?)";

    @Language("SQL")
    private static final String INSERT_LOCATION = "INSERT INTO `locations` (`home_id`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)";
    @Language("SQL")
    private static final String DELETE_HOME = "DELETE FROM `homes` WHERE `clan_tag`=? AND `name`=?";
    private final Handle handle;
    private final @Nullable int clanId;

    public SqlClanEdition(@NotNull Handle handle, int clanId) {
        this.handle = handle;
        this.clanId = clanId;
    }
    @Override
    public ClanEdition rename(@NotNull Component displayName) {
        handle.createUpdate(UPDATE_DISPLAY_NAME)
                .bind(0, displayName)
                .bind(1, clanId)
                .execute();
        return this;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        handle.createUpdate(UPDATE_STATISTIC)
                .bind(0, value)
                .bind(1, clanId)
                .bind(2, type).execute();
        return this;
    }

    @Override
    public ClanEdition incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition removeStatistic(@NotNull StatisticType type) {
        handle.createUpdate(DELETE_STATISTIC)
                .bind(0, clanId)
                .bind(1, type).execute();
        return this;

    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        int updateCount = handle.createUpdate(INSERT_MEMBER)
                .bind(0, clanId)
                .bind(1, member.uniqueId())
                .bind(2, member.role()).execute();
        if(updateCount != 1) {
            handle.rollback();
            throw new IllegalArgumentException("Member with given uuid already in the clan");
        }
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        handle.createUpdate(DELETE_MEMBER).bind(0, member.uniqueId()).execute();
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID member, @NotNull Consumer<MemberEdition> consumer) {
        consumer.accept(new SqlMemberEdition(handle, clanId, member));
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        Location loc = home.location();
        Integer homeId = handle.createUpdate(INSERT_HOME)
                .bind(0, clanId)
                .bind(1, home.name())
                .bind(2, home.creator())
                .bind(3, home.displayName())
                .bind(4, home.icon())
                .executeAndReturnGeneratedKeys("id").mapTo(Integer.class).findFirst().orElse(null);

        if(homeId == null) {
            handle.rollback();
            throw new IllegalArgumentException("home already exists");
        }

        handle.createUpdate(INSERT_LOCATION)
                .bind(0, homeId)
                .bind(1, loc.getBlockX())
                .bind(2, loc.getBlockZ())
                .bind(3, loc.getBlockZ())
                .bind(4, loc.getWorld().getName()).execute();


        return this;
    }



    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        handle.createUpdate(DELETE_HOME)
                .bind(0, clanId)
                .bind(1, home.name());
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        consumer.accept(new SqlHomeEdition(handle, clanId, name));
        return this;
    }

}
