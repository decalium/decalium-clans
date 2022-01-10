package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.types.tuples.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.CachingClanManager;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
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

public class HomeCommand extends AbstractCommand {
    private final ClanBuilderFactory builderFactory;

    public HomeCommand(@NotNull Logger logger,
                       @NotNull CachingClanManager clanManager,
                       @NotNull ClansConfig clansConfig,
                       @NotNull MessagesConfig messages,
                       @NotNull FactoryOfTheFuture futuresFactory,
                       @NotNull ClanBuilderFactory builderFactory) {
        super(logger, clanManager, clansConfig, messages, futuresFactory);
        this.builderFactory = builderFactory;
    }

    @Override
    public void register(CommandManager<CommandSender> manager) {

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
            if(clan.getHome(name) != null) {
                player.sendMessage(messages.commands().home().homeAlreadyExists().with("name", name));
                return nullFuture();
            }

            return clanManager.editClan(clan, clanEditor -> clanEditor.addHome(home));
        });

    }

    private void deleteHome(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String name = context.get("name");
        requireHome(player, name).thenComposeSync(pair -> {
            Clan clan = pair.getFirst();
            ClanHome home = pair.getSecond();

            if(clan == null || home == null) return nullFuture();

            ClanMember member = getMember(clan, player);
            ClanMember homeOwner = getMember(clan, home.getCreator());

            if(!Objects.equals(member, homeOwner) && !member.hasPermission(ClanPermission.EDIT_OTHERS_HOMES)) {
                player.sendMessage(messages.noClanPermission());
                return nullFuture();
            }

            return clanManager.editClan(clan, clanEditor -> clanEditor.removeHome(home));

        }).thenAccept(c -> {
            if(c != null) player.sendMessage(messages.commands().home().deleted());
        });
    }



    private CentralisedFuture<Pair<@Nullable Clan, @Nullable ClanHome>> requireHome(@NotNull Player player, @NotNull String homeName) {
        return this.requireClan(player)
                .thenApply(clan ->{
                    if(clan == null) return Pair.of(null, null);
                    ClanHome home = clan.getHome(homeName);
                    if(home == null) {
                        player.sendMessage(messages.commands().home().homeNotFound().with("name", homeName));
                    }
                    return Pair.of(clan, home);
                });
    }
}
