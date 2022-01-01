package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractCommand {


    protected final ClanManager clanManager;
    protected final ClansConfig clansConfig;
    protected final MessagesConfig messages;
    protected final FactoryOfTheFuture futuresFactory;

    public AbstractCommand(ClanManager clanManager,
                           ClansConfig clansConfig,
                           MessagesConfig messages,
                           FactoryOfTheFuture futuresFactory) {
        this.clanManager = clanManager;
        this.clansConfig = clansConfig;
        this.messages = messages;
        this.futuresFactory = futuresFactory;
    }



    public abstract void register(CommandManager<CommandSender> manager);


    @NotNull
    protected ClanMember getMember(@NotNull Clan clan, @NotNull UUID uuid) {
        return Objects.requireNonNull(clan.getMember(uuid));
    }

    @NotNull
    protected ClanMember getMember(@NotNull Clan clan, @NotNull OfflinePlayer player) {
        return getMember(clan, player.getUniqueId());
    }

    protected boolean checkPermission(Player player, ClanMember member, ClanPermission permission) {
        boolean has = member.hasPermission(permission);
        if(!has) player.sendMessage(messages.noClanPermission());
        return has;
    }

    protected boolean checkClan(Player player, Clan clan) {
        boolean in = clan != null;
        if(!in) player.sendMessage(messages.notInTheClan());
        return in;
    }

    protected <T> CentralisedFuture<T> nullFuture() {
        return futuresFactory.completedFuture(null);
    }








}
