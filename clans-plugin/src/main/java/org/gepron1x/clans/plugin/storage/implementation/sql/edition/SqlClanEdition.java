package org.gepron1x.clans.plugin.storage.implementation.sql.edition;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableHomes;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableMembers;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableStatistics;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
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
    private static final String DELETE_HOME = "DELETE FROM `homes` WHERE `clan_id`=? AND `name`=?";
    private final Handle handle;
    private final int clanId;

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
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {;
        return setStatistics(Map.of(type, value));
    }

    @Override
    public ClanEdition setStatistics(@NotNull Map<StatisticType, Integer> statistics) {
        new SavableStatistics(handle, clanId, statistics).execute();
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
        return addMembers(Collections.singleton(member));
    }

    @Override
    public ClanEdition addMembers(@NotNull Collection<ClanMember> members) {
        new SavableMembers(handle, clanId, members).execute();
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
        return addHomes(Collections.singleton(home));
    }

    @Override
    public ClanEdition addHomes(@NotNull Collection<ClanHome> homes) {
        new SavableHomes(handle, clanId, homes).execute();
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        handle.createUpdate(DELETE_HOME)
                .bind(0, clanId)
                .bind(1, home.name()).execute();
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        consumer.accept(new SqlHomeEdition(handle, clanId, name));
        return this;
    }

}
