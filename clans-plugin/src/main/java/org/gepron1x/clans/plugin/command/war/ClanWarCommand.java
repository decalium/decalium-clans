package org.gepron1x.clans.plugin.command.war;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.reference.TagClanReference;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.chat.resolvers.ClanTagResolver;
import org.gepron1x.clans.plugin.chat.resolvers.PrefixedTagResolver;
import org.gepron1x.clans.plugin.command.AbstractClanCommand;
import org.gepron1x.clans.plugin.command.ClanExecutionHandler;
import org.gepron1x.clans.plugin.command.PermissiveClanExecutionHandler;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.war.Wars;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public final class ClanWarCommand extends AbstractClanCommand {
    private final Wars wars;
    private final Table<String, String, ClanWarRequest> requestMap = HashBasedTable.create();

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
                .argument(StringArgument.of("tag"))
                .handler(clanExecutionHandler(new PermissiveClanExecutionHandler(clanExecutionHandler(this::requestWar), ClanPermission.SEND_WAR_REQUEST, this.messages)))
        );

        manager.command(builder.literal("accept").permission("clans.war.accept")
                .argument(StringArgument.of("tag"))
                .handler(clanExecutionHandler(new PermissiveClanExecutionHandler(clanExecutionHandler(this::acceptWar), ClanPermission.ACCEPT_WAR, this.messages)))
        );

    }


    private void requestWar(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanReference enacted = new TagClanReference(this.clanRepository, clan.tag());
        String tag = context.get("tag");
        ClanReference victim = new TagClanReference(this.clanRepository, tag);

        if(victim.cached().isEmpty()) {
            player.sendMessage(this.messages.noOnlinePlayers());
            return;
        }


        victim.cached().map(Clan::members).ifPresent(members -> {
            TagResolver resolver = PrefixedTagResolver.prefixed(ClanTagResolver.clan(clan), "clan");
            for(ClanMember member : members) {
                Player p = member.asPlayer(player.getServer());
                if(p == null) return;
                p.sendMessage(this.messages.commands().wars().requestMessage().with(resolver));
                if(member.hasPermission(ClanPermission.ACCEPT_WAR)) {
                    p.sendMessage(this.messages.commands().wars().acceptMessage().with(resolver));
                }
            }
        });

        player.sendMessage(this.messages.commands().wars().requestSent());

        ClanWarRequest request = new ClanWarRequest(wars, enacted, victim);
        this.requestMap.put(tag, clan.tag(), request);

    }

    private void acceptWar(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        String tag = context.get("tag");
        ClanWarRequest request = this.requestMap.get(clan.tag(), tag);
        if(request == null) {
            player.sendMessage(this.messages.commands().wars().noRequests().with("tag", tag));
            return;
        }
        request.accept();
        requestMap.remove(clan.tag(), tag);
    }
}
