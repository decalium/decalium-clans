package org.gepron1x.clans.plugin.command.edition;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public final class UserFriendlyClanEdition implements ClanEdition {


    private final Clan clan;
    private final ClansConfig clansConfig;
    private final MessagesConfig messagesConfig;

    public UserFriendlyClanEdition(Clan clan, ClansConfig clansConfig, MessagesConfig messagesConfig) {
        this.clan = clan;
        this.clansConfig = clansConfig;
        this.messagesConfig = messagesConfig;
    }

    @Override
    public ClanEdition rename(@NotNull Component displayName) {
        return this;
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
        clan.member(member.uniqueId()).ifPresent(ignored -> {
            failed(this.messagesConfig.playerIsAlreadyInClan().with("name", member.renderName(Bukkit.getServer())));
        });
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        if(clan.homes().size() >= clansConfig.homes().maxHomes()) {
            failed(this.messagesConfig.commands().home().tooManyHomes());
        }
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        return this;
    }

    private void failed(ComponentLike reason) {
        throw new ValidationFailedException(reason);
    }
}
