package org.gepron1x.clans.plugin.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.Validations;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.command.argument.ComponentArgument;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.Collections;
import java.util.List;

public class HomeCommand extends AbstractClanCommand {
    private final ClanBuilderFactory builderFactory;

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

        var name = StringArgument.<CommandSender>newBuilder("name").withSuggestionsProvider(this::homesCompletion);

        Command.Builder<CommandSender> builder = manager.commandBuilder("clan")
                .literal("home")
                .senderType(Player.class);

        manager.command(builder.literal("create")
                .permission("clans.home.create")
                .argument(StringArgument.of("name"))
                .argument(ComponentArgument.optional("display_name", StringArgument.StringMode.GREEDY))
                .handler(
                       clanExecutionHandler(
                                new PermissiveClanExecutionHandler(this::createHome, ClanPermission.ADD_HOME, this.messages)
                               )
                )
        );

        manager.command(builder.literal("delete")
                .permission("clans.home.delete")
                .argument(name)
                .handler(clanExecutionHandler(new PermissiveClanExecutionHandler(
                        new HomeRequiredExecutorHandler(checkHomeOwner(this::deleteHome), ctx -> ctx.get("name"), this.messages),
                        ClanPermission.REMOVE_HOME,
                        this.messages

                )))
        );

        manager.command(builder.literal("teleport")
                .permission("clans.home.teleport")
                .argument(name)
                .handler(clanExecutionHandler(
                        new HomeRequiredExecutorHandler(this::teleportToHome, ctx -> ctx.get("name"), this.messages)
                        )
                )
        );

        manager.command(builder.literal("rename").permission("clans.home.rename")
                .argument(name)
                .handler(clanExecutionHandler(
                        new HomeRequiredExecutorHandler(checkHomeOwner(this::renameHome), ctx -> ctx.get("name"), this.messages)
                )
                )
        );

    }




    private void createHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("name");


        if(!Validations.checkHomeName(name)) {
            player.sendMessage(this.messages.commands().home().invalidHomeName());
            return;
        }

        Component displayName = context.<Component>getOptional("display_name").orElseGet(() -> Component.text(name, NamedTextColor.GRAY));
        ItemStack icon = player.getInventory().getItemInMainHand();
        if(icon.getType().isAir()) {
            player.sendMessage(this.messages.commands().home().holdAnItem());
            return;
        }
        Location location = player.getLocation();

        ClanHome home = builderFactory.homeBuilder()
                .name(name)
                .creator(player.getUniqueId())
                .displayName(displayName)
                .location(location)
                .icon(icon).build();

        Clan clan = context.get(ClanExecutionHandler.CLAN);
        if(clan.homes().size() >= this.clansConfig.homes().maxHomes()) {
            player.sendMessage(messages.commands().home().tooManyHomes());
            return;
        }
        if(clan.home(name).isPresent()) {
            player.sendMessage(messages.commands().home().homeAlreadyExists().with("name", name));
            return;
        }
       clan.edit(edition -> edition.addHome(home)).thenAccept(c -> player.sendMessage(messages.commands().home().created()))
               .exceptionally(this::exceptionHandler);
    }

    private void deleteHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();

        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanHome home = context.get(HomeRequiredExecutorHandler.HOME);

       clan.edit(edition -> edition.removeHome(home)).thenAccept(c -> {
            if(c != null) player.sendMessage(messages.commands().home().deleted());
        }).exceptionally(this::exceptionHandler);
    }

    private void teleportToHome(CommandContext<CommandSender> context) {

        Player player = (Player) context.getSender();
        ClanHome home = context.get(HomeRequiredExecutorHandler.HOME);
        player.teleportAsync(home.location()).thenAccept(bool -> {
            if(bool) player.sendMessage(this.messages.commands().home().teleported().with("name", home.displayName()));
        }).exceptionally(this::exceptionHandler);
    }

    private void renameHome(CommandContext<CommandSender> context) {
        Clan clan = context.get(ClanExecutionHandler.CLAN);
        ClanHome home = context.get(HomeRequiredExecutorHandler.HOME);
        Component name = context.get("name");
        clan.edit(edition -> edition.editHome(home.name(), homeEdition -> {
            homeEdition.rename(name);
        })).thenAccept(c -> {
            context.getSender().sendMessage(this.messages.commands().home().renamed());
        }).exceptionally(this::exceptionHandler);
    }


     private List<String> homesCompletion(CommandContext<CommandSender> context, String s) {
        if (!(context.getSender() instanceof Player player)) return Collections.emptyList();
        return clanRepository.userClanIfCached(player.getUniqueId()).map(Clan::homes)
                .map(homes -> homes.stream().map(ClanHome::name).toList()).orElse(Collections.emptyList());

    }


    private CommandExecutionHandler<CommandSender> checkHomeOwner(CommandExecutionHandler<CommandSender> delegate) {
        return ctx -> {
            ClanMember member = ctx.get(ClanExecutionHandler.CLAN_MEMBER);
            ClanHome home = ctx.get(HomeRequiredExecutorHandler.HOME);
            if(!home.creator().equals(member.uniqueId()) && !member.hasPermission(ClanPermission.EDIT_OTHERS_HOMES)) {
                ctx.getSender().sendMessage(this.messages.noClanPermission());
                return;
            }
            delegate.execute(ctx);
        };
    }




}
