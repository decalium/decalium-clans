package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Objects;

public class MemberCommand extends AbstractCommand {

    private final ClanManager manager;
    private final RoleRegistry roleRegistry;
    private final FactoryOfTheFuture futuresFactory;

    public MemberCommand(@NotNull ClanManager manager, @NotNull RoleRegistry roleRegistry, @NotNull FactoryOfTheFuture futuresFactory) {

        this.manager = manager;
        this.roleRegistry = roleRegistry;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan").literal("member");
        manager.command(builder
                .literal("set")
                .literal("role")
                .permission("clans.set.role")
                .argument(OfflinePlayerArgument.of("member"))
                .argument(manager.argumentBuilder(ClanRole.class, "role"))
                .handler(this::setRole)
        );

    }


    private void setRole(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        OfflinePlayer memberPlayer = context.get("member");
        ClanRole role = context.get("role");
        this.manager.getUserClan(player.getUniqueId())
                .thenComposeSync(clan -> {
                    if(clan == null) {
                        player.sendMessage(Component.text("You are not in the clan!"));
                        return futuresFactory.completedFuture(null);
                    }
                    ClanMember member = Objects.requireNonNull(clan.getMember(player.getUniqueId()));
                    if(!member.hasPermission(ClanPermission.SET_ROLE)) {
                        player.sendMessage(Component.text("You cannot set other's members roles!"));
                        return futuresFactory.completedFuture(null);
                    }

                    System.out.println(clan);



                    ClanMember other = clan.getMember(memberPlayer.getUniqueId());
                    System.out.println(other);

                    if(other == null) {
                        player.sendMessage(Component.text("Specified player is not a member of the clan."));
                        return futuresFactory.completedFuture(null);
                    }
                    return this.manager.editClan(clan,
                            clanEditor -> clanEditor
                                    .editMember(memberPlayer.getUniqueId(), memberEditor -> memberEditor.setRole(role)));
                }).thenAcceptSync(clan -> {
                    if(clan != null) player.sendMessage(Component.text("Role was set successfully."));
                });

    }
}
