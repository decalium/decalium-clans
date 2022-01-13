package org.gepron1x.clans.plugin.storage.implementation.sql.editor;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Handle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class SqlClanEditor implements ClanEditor {
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
    private final Clan clan;

    public SqlClanEditor(@NotNull Handle handle, @NotNull Clan clan) {
        this.handle = handle;
        this.clan = clan;
    }
    @Override
    public ClanEditor setDisplayName(@NotNull Component displayName) {
        handle.createUpdate(UPDATE_DISPLAY_NAME)
                .bind(0, displayName)
                .bind(1, clan.getId())
                .execute();
        return this;
    }

    @Override
    public ClanEditor setStatistic(@NotNull StatisticType type, int value) {
        handle.createUpdate(UPDATE_STATISTIC)
                .bind(0, value)
                .bind(1, clan.getId())
                .bind(2, type).execute();
        return this;
    }

    @Override
    public ClanEditor incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEditor removeStatistic(@NotNull StatisticType type) {
        handle.createUpdate(DELETE_STATISTIC)
                .bind(0, clan.getId())
                .bind(1, type).execute();
        return this;

    }

    @Override
    public ClanEditor addMember(@NotNull ClanMember member) {
        int updateCount = handle.createUpdate(INSERT_MEMBER)
                .bind(0, clan.getId())
                .bind(1, member.getUniqueId())
                .bind(2, member.getRole()).execute();
        if(updateCount != 0) {
            handle.rollback();
            throw new IllegalArgumentException("Member with given uuid already in the clan");
        }
        return this;
    }

    @Override
    public ClanEditor removeMember(@NotNull ClanMember member) {
        handle.createUpdate(DELETE_MEMBER).bind(0, member.getUniqueId()).execute();
        return this;
    }

    @Override
    public ClanEditor editMember(@NotNull UUID member, @NotNull Consumer<MemberEditor> consumer) {
        consumer.accept(new SqlMemberEditor(handle, clan, Objects.requireNonNull(clan.getMember(member))));
        return this;
    }

    @Override
    public ClanEditor addHome(@NotNull ClanHome home) {
        Location loc = home.getLocation();
        Integer homeId = handle.createUpdate(INSERT_HOME)
                .bind(0, clan.getId())
                .bind(1, home.getName())
                .bind(2, home.getCreator())
                .bind(3, home.getDisplayName())
                .bind(4, home.getIcon())
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
    public ClanEditor removeHome(@NotNull ClanHome home) {
        handle.createUpdate(DELETE_HOME)
                .bind(0, clan.getId())
                .bind(1, home.getName());
        return this;
    }

    @Override
    public ClanEditor editHome(@NotNull String name, @NotNull Consumer<HomeEditor> consumer) {
        consumer.accept(new SqlHomeEditor(handle, clan, Objects.requireNonNull(clan.getHome(name))));
        return this;
    }

}
