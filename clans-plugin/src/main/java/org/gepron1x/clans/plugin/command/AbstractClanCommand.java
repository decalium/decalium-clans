package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;
import java.util.UUID;

public abstract class AbstractClanCommand {


    private final Logger logger;
    protected final CachingClanRepository clanManager;
    protected final ClansConfig clansConfig;
    protected final MessagesConfig messages;
    protected final FactoryOfTheFuture futuresFactory;

    public AbstractClanCommand(Logger logger, CachingClanRepository clanManager,
                               ClansConfig clansConfig,
                               MessagesConfig messages,
                               FactoryOfTheFuture futuresFactory) {
        this.logger = logger;
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

    protected CentralisedFuture<@Nullable Clan> requireClan(@NotNull Player player) {
        return this.clanManager.getUserClan(player.getUniqueId()).thenApply(clan -> {
            checkClan(player, clan);
            return clan;
        });
    }

    protected <T> CentralisedFuture<T> nullFuture() {
        return futuresFactory.completedFuture(null);
    }
    
    protected <T> T exceptionHandler(Throwable throwable) {
        logger.error("A future completed exceptionally: ", throwable);
        throwable.printStackTrace();
        return null;
    }








}
