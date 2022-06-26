package org.gepron1x.clans.plugin.command.edition;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public final class UserFriendlyClanEdition implements ClanEdition {


    private final PlayerReference player;
    private final ClansConfig clansConfig;
    private final MessagesConfig messagesConfig;

    public UserFriendlyClanEdition(PlayerReference player, ClansConfig clansConfig, MessagesConfig messagesConfig) {
        this.player = player;
        this.clansConfig = clansConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public ClanEdition rename(@NotNull Component displayName) {
        return null;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        return this;
    }

    @Override
    public ClanEdition incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition removeStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        return null;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        return null;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        return null;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        return null;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        return null;
    }
}
