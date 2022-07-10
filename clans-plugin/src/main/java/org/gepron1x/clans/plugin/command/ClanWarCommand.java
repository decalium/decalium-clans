package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.reference.TagClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.Wars;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class ClanWarCommand extends AbstractClanCommand {
    private final Wars wars;

    public ClanWarCommand(Logger logger,
                          CachingClanRepository clanRepository,
                          ClansConfig clansConfig,
                          MessagesConfig messages,
                          FactoryOfTheFuture futuresFactory,
                          Wars wars
                          ) {
        super(logger, clanRepository, clansConfig, messages, futuresFactory);
        this.wars = wars;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("war").senderType(Player.class);


        manager.command(builder.literal("request").permission("clans.war.request")
                .argument(StringArgument.of("tag")).handler(clanExecutionHandler(this::requestWar)));

    }


    private void requestWar(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        ClanReference enacted = new TagClanReference(this.clanRepository, context.get(ClanExecutionHandler.CLAN).tag());
        String tag = context.get("tag");
        ClanReference victim = new TagClanReference(this.clanRepository, tag);

        if(victim.cached().isEmpty()) {
            player.sendMessage(Component.text("No online players!"));
            return;
        }

        War war = wars.create(wars.createTeam(enacted), wars.createTeam(victim));
        wars.start(war);



    }
}
