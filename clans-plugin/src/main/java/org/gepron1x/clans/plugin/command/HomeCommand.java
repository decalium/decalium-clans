package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class HomeCommand extends AbstractClanCommand {
    private final ClanBuilderFactory builderFactory;

    private record ClanAndHome(@Nullable Clan clan, @Nullable ClanHome home) {
        static final ClanAndHome EMPTY = new ClanAndHome(null, null);
    }

    public HomeCommand(@NotNull Logger logger,
                       @NotNull CachingClanRepository clanManager,
                       @NotNull ClansConfig clansConfig,
                       @NotNull MessagesConfig messages,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory) {
        super(logger, clanManager, clansConfig, messages, futuresFactory);
        this.builderFactory = builderFactory;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan")
                .literal("home")
                .senderType(Player.class);

        manager.command(builder.literal("create")
                .permission("clans.home.create")
                .argument(StringArgument.of("name"))
                .argument(ComponentArgument.optional("display_name", StringArgument.StringMode.GREEDY))
                .handler(this::createHome)

        );

        manager.command(builder.literal("delete")
                .permission("clans.home.delete")
                .argument(StringArgument.of("name"))
                .handler(this::deleteHome)
        );

        manager.command(builder.literal("teleport")
                .permission("clans.home.teleport")
                .argument(StringArgument.of("name"))
                .handler(this::teleportToHome)
        );

    }

    private void createHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("name");
        Component displayName = context.<Component>getOptional("display_name").orElseGet(() -> Component.text(name, NamedTextColor.GRAY));
        ItemStack icon = player.getInventory().getItemInMainHand();
        Location location = player.getLocation();

        ClanHome home = builderFactory.homeBuilder()
                .name(name)
                .creator(player.getUniqueId())
                .displayName(displayName)
                .location(location)
                .icon(icon).build();

        requireClan(player).thenComposeSync(clan -> {
            if(clan == null) return nullFuture();
            ClanMember member = getMember(clan, player);
            if(!checkPermission(player, member, ClanPermission.ADD_HOME)) return nullFuture();
            if(clan.getHome(name) != null) {
                player.sendMessage(messages.commands().home().homeAlreadyExists().with("name", name));
                return nullFuture();
            }

            return clanManager.editClan(clan, clanEditor -> clanEditor.addHome(home));
        }).thenAccept(clan -> {
            player.sendMessage(messages.commands().home().created());
        }).exceptionally(this::exceptionHandler);

    }

    private void deleteHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("name");
        requireHome(player, name).thenComposeSync(pair -> {
            Clan clan = pair.clan();
            ClanHome home = pair.home();

            if(clan == null || home == null) return nullFuture();

            ClanMember member = getMember(clan, player);
            ClanMember homeOwner = getMember(clan, home.getCreator());

            if(!member.equals(homeOwner) && !member.hasPermission(ClanPermission.EDIT_OTHERS_HOMES)) {
                player.sendMessage(messages.noClanPermission());
                return nullFuture();
            }

            return clanManager.editClan(clan, clanEditor -> clanEditor.removeHome(home));

        }).thenAccept(c -> {
            if(c != null) player.sendMessage(messages.commands().home().deleted());
        }).exceptionallyCompose(this::exceptionHandler);
    }

    private void teleportToHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("name");
        requireHome(player, name).thenComposeSync(pair -> {
            ClanHome home = pair.home();
            if(home == null) return nullFuture();
            return player.teleportAsync(home.getLocation());
        }).thenAccept(success -> {
            if(success) player.sendMessage(Component.text("Teleported successfully"));
        }).exceptionally(this::exceptionHandler);
    }



    private CentralisedFuture<ClanAndHome> requireHome(@NotNull Player player, @NotNull String homeName) {
        return this.requireClan(player)
                .thenApply(clan -> {
                    if(clan == null) return ClanAndHome.EMPTY;
                    ClanHome home = clan.getHome(homeName);
                    if(home == null) {
                        player.sendMessage(messages.commands().home().homeNotFound().with("name", homeName));
                    }
                    return new ClanAndHome(clan, home);
                });
    }
}
