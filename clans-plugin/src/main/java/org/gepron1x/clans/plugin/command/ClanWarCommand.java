package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.CachingClanManager;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.war.ClanWarManager;
import org.gepron1x.clans.plugin.war.Preparations;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class ClanWarCommand extends AbstractClanCommand {
    private final Preparations preparations;
    private final ClanWarManager warManager;

    public ClanWarCommand(Logger logger,
                          CachingClanManager clanManager,
                          ClansConfig clansConfig,
                          MessagesConfig messages,
                          FactoryOfTheFuture futuresFactory,
                          Preparations preparations,
                          ClanWarManager warManager) {
        super(logger, clanManager, clansConfig, messages, futuresFactory);
        this.preparations = preparations;
        this.warManager = warManager;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {

    }

    private void sendClanWarRequest(CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();
        String clanTag = context.get("clan");
        CentralisedFuture<Clan> playerClan = requireClan(player);
        CentralisedFuture<Clan> otherClan = clanManager.getClan(clanTag);

        futuresFactory.allOf(playerClan, otherClan).thenAcceptSync(ignored -> {
            Clan clan = playerClan.join();
            Clan other = otherClan.join();
            if(clan == null) return;
            if(other == null) {
                player.sendMessage(Component.text("No clan with given tag exists."));
                return;
            }



        });

    }
}
