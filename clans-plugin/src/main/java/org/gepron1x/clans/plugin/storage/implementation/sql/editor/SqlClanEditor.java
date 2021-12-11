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
import org.jdbi.v3.core.statement.Update;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class SqlClanEditor implements ClanEditor {
    private static final String UPDATE_DISPLAY_NAME = "UPDATE clans SET display_name=<name> WHERE tag=<tag>";
    private static final String UPDATE_STATISTIC = "UPDATE stats SET value=<value> WHERE clan_tag=<tag> AND type=<type>";
    private static final String DELETE_STATISTIC = "DELETE FROM stats WHERE clan_tag=<tag> AND type=<type>";
    private static final String INSERT_MEMBER = "INSERT INTO members (clan_tag, uuid, role) VALUES (<clan_tag>, <uuid>, <role>)";
    private static final String DELETE_MEMBER = "DELETE FROM members WHERE uuid=<uuid>";
    private static final String INSERT_HOME = "INSERT INTO homes (clan_tag, name, creator, display_name, icon, x, y, z, world) VALUES (<clan_tag>, <name>, <creator>, <display_name>, <location>, <icon>, <x>, <y>, <z>, <world>)";
    private static final String DELETE_HOME = "DELETE FROM homes WHERE clan_tag=<clan_tag> AND name=<name>";
    private final Handle handle;
    private final Clan clan;

    public SqlClanEditor(@NotNull Handle handle, @NotNull Clan clan) {
        this.handle = handle;
        this.clan = clan;
    }
    @Override
    public ClanEditor setDisplayName(@NotNull Component displayName) {
        handle.createUpdate(UPDATE_DISPLAY_NAME)
                .bind("name", displayName)
                .bind("tag", clan.getTag())
                .execute();
        return this;
    }

    @Override
    public ClanEditor setStatistic(@NotNull StatisticType type, int value) {
        handle.createUpdate(UPDATE_STATISTIC)
                .bind("value", value)
                .bind("clan_tag", clan.getTag())
                .bind("type", type).execute();
        return this;
    }

    @Override
    public ClanEditor incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEditor removeStatistic(@NotNull StatisticType type) {
        handle.createUpdate(DELETE_STATISTIC)
                .bind("clan_tag", clan.getTag())
                .bind("type", type).execute();
        return this;

    }

    @Override
    public ClanEditor addMember(@NotNull ClanMember member) {
        handle.createUpdate(INSERT_MEMBER)
                .bind("clan_tag", clan.getTag())
                .bind("uuid", member.getUniqueId())
                .bind("role", member.getRole()).execute();
        return this;
    }

    @Override
    public ClanEditor removeMember(@NotNull ClanMember member) {
        handle.createUpdate(DELETE_MEMBER).bind("uuid", member.getUniqueId()).execute();
        return this;
    }

    @Override
    public ClanEditor editMember(@NotNull ClanMember member, @NotNull Consumer<MemberEditor> consumer) {
        consumer.accept(new SqlMemberEditor(handle, clan, member));
        return this;
    }

    @Override
    public ClanEditor addHome(@NotNull ClanHome home) {
        Update update = handle.createUpdate(INSERT_HOME)
                .bind("clan_tag", clan.getTag())
                .bind("name", home.getName())
                .bind("creator", home.getCreator())
                .bind("display_name", home.getDisplayName())
                .bind("icon", home.getIcon());
        bindLocation(update, home.getLocation());
        update.execute();
        return this;
    }

    private void bindLocation(Update update, Location location) {
        update.bind("x", location.getBlockX())
                .bind("y", location.getBlockY())
                .bind("z", location.getBlockZ())
                .bind("world", location.getWorld());

    }

    @Override
    public ClanEditor removeHome(@NotNull ClanHome home) {
        handle.createUpdate(DELETE_HOME)
                .bind("clan_tag", clan.getTag())
                .bind("name", home.getName());
        return this;
    }

    @Override
    public ClanEditor editHome(@NotNull ClanHome home, @NotNull Consumer<HomeEditor> consumer) {
        consumer.accept(new SqlHomeEditor(handle, clan, home));
        return this;
    }

    @Override
    public Clan getTarget() {
        return clan;
    }
}
