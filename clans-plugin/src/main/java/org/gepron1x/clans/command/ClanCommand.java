package org.gepron1x.clans.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.command.argument.ComponentArgument;
import org.gepron1x.clans.config.ClansConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class ClanCommand extends AbstractCommand {

    private static final String TAG = "tag";

    private final RoleRegistry roleRegistry;
    private final ClanManager manager;
    private final ClansConfig config;
    private final ClanBuilderFactory builderFactory;

    public ClanCommand(@NotNull ClanBuilderFactory builderFactory, @NotNull RoleRegistry roleRegistry, @NotNull ClanManager manager, @NotNull ClansConfig config) {
        this.builderFactory = builderFactory;
        this.roleRegistry = roleRegistry;
        this.manager = manager;
        this.config = config;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan");
        manager.command(builder.literal("create")
                .permission("clans.create")
                .argument(StringArgument.of("tag"))
                .argument(ComponentArgument.optional("display_name", StringArgument.StringMode.GREEDY))
                .handler(this::createClan)
        );

        manager.command(builder.literal("delete")
                .permission("clans.delete")
                .handler(this::deleteClan)
        );

        manager.command(builder.literal("myclan").permission("clans.myclan").handler(this::myClan));
    }


    private void createClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String tag = context.get("tag");
        Component displayName = requireNonNull(context.getOrSupplyDefault("display_name", () -> Component.text(tag)));
        UUID uuid = player.getUniqueId();
        DraftClan clan = builderFactory.draftClanBuilder().tag(tag)
                .displayName(displayName)
                .owner(uuid)
                .addMember(builderFactory.memberBuilder()
                        .uuid(uuid)
                        .role(roleRegistry.getOwnerRole())
                        .build()
                ).build();

        manager.createClan(clan).thenAccept(result -> {
            if(result.isSuccess()) {
                player.sendMessage(Component.text("Clan created successfully.", NamedTextColor.GREEN));
            } else {
                player.sendMessage(Component.text().append(Component.text("Error happened while creating a clan! ", NamedTextColor.RED)).append(Component.text(result.status().name())));
            }
        });

    }

    private void myClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();
        manager.getUserClan(uuid).thenAccept(clan -> {
            if(clan == null) {
                player.sendMessage(Component.text("You are not in the clan.", NamedTextColor.RED));
                return;
            }
            player.sendMessage(Component.text().color(NamedTextColor.AQUA).append(Component.text("You are a member of ")).append(clan.getDisplayName()).append(Component.text("clan")));
        });
    }

    private void deleteClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        UUID uuid = player.getUniqueId();
        manager.getUserClan(uuid).thenAccept(clan -> {
            if(clan == null) {
                player.sendMessage(Component.text("You are not in the clan.", NamedTextColor.RED));
                return;
            }
            if(!clan.getOwner().equals(uuid)) {
                player.sendMessage(Component.text("Вы не владелец этого клана!"));
                return;
            }
            manager.removeClan(clan).thenAccept(bool -> player.sendMessage("Клан успешно удалён."));
        });
    }
}
