package org.gepron1x.clans.plugin.command;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.keys.CloudKey;
import cloud.commandframework.keys.SimpleCloudKey;
import io.leangen.geantyref.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.gepron1x.clans.api.ClanRepository;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.plugin.config.MessagesConfig;

public final class ClanExecutionHandler implements CommandExecutionHandler<CommandSender> {

    public static final CloudKey<Clan> CLAN = SimpleCloudKey.of("decalium_clan", TypeToken.get(Clan.class));
    public static final CloudKey<ClanMember> CLAN_MEMBER = SimpleCloudKey.of("decalium_member", TypeToken.get(ClanMember.class));

    private final CommandExecutionHandler<CommandSender> delegate;
    private final ClanRepository repository;
    private final MessagesConfig messages;

    public ClanExecutionHandler(CommandExecutionHandler<CommandSender> delegate,
                                ClanRepository repository,
                                MessagesConfig messages) {
        this.delegate = delegate;
        this.repository = repository;
        this.messages = messages;
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        Player player = (Player) commandContext.getSender();
        repository.requestUserClan(player).thenAcceptSync(optClan -> {
            optClan.ifPresentOrElse(clan -> {
                commandContext.store(CLAN, clan);
                commandContext.store(CLAN_MEMBER, clan.member(player).orElseThrow());
                this.delegate.execute(commandContext);
            }, () -> player.sendMessage(messages.notInTheClan()));
        }).exceptionally(t -> {
            t.printStackTrace();
            return null;
        });
    }
}
