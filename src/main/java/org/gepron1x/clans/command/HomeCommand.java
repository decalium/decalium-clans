package org.gepron1x.clans.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import com.sk89q.worldguard.WorldGuard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.Permissions;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.clan.home.ClanHomeCache;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.util.BlockPlaceListener;
import org.jetbrains.annotations.NotNull;

public class HomeCommand extends BaseClanCommand {
    private static final String HOME_NAME = "home_name";
    private final BlockPlaceListener blockPlaceListener;
    private final ClanHomeCache homeCache;
    private final WorldGuard worldGuard;


    public HomeCommand(ClanManager manager, MessagesConfig messages,  BlockPlaceListener blockPlaceListener, ClanHomeCache homeCache, WorldGuard worldGuard) {
        super(manager, messages);
        this.blockPlaceListener = blockPlaceListener;
        this.homeCache = homeCache;
        this.worldGuard = worldGuard;
    }

    private void create(CommandContext<CommandSender> ctx) {
        Player executor = (Player) ctx.getSender();
        String homeName = ctx.get(HOME_NAME);
        Clan clan = getClan(executor);
        if(homeCache.getOwningClan(homeName) != null) {
            executor.sendMessage(messages.homes().homeWithNameAlreadyExists().with("name", homeName));
            return;
        }
        ClanHome ch = ClanHome.builder().name(homeName)
                .displayName(Component.text(homeName, NamedTextColor.GRAY))
                .icon(new ItemStack(Material.AIR)).build();

    }


    @Override
    public void register(@NotNull CommandManager<CommandSender> manager) {
        Command.Builder<CommandSender> home = manager.commandBuilder("clan").literal("home");
        manager.command(home.literal("create")
                .senderType(Player.class)
                .permission(Permissions.CREATE_HOME)
                .meta(ClanPermission.CLAN_PERMISSION, ClanPermission.CREATE_HOME)
                .argument(StringArgument.of(HOME_NAME))
                .handler(this::create)
        );
    }
}
