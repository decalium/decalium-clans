package org.gepron1x.clans.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldguard.WorldGuard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.clan.home.ClanHomeCache;
import org.gepron1x.clans.ClanManager;
import org.gepron1x.clans.util.BlockPlaceListener;

@CommandAlias("clan")
@Subcommand("home")
public class HomeCommand extends BaseClanCommand {
    private final BlockPlaceListener blockPlaceListener;
    private final ClanHomeCache homeCache;
    private final WorldGuard worldGuard;


    public HomeCommand(ClanManager manager, MessagesConfig messages,  BlockPlaceListener blockPlaceListener, ClanHomeCache homeCache, WorldGuard worldGuard) {
        super(manager, messages);
        this.blockPlaceListener = blockPlaceListener;
        this.homeCache = homeCache;
        this.worldGuard = worldGuard;
    }

    @Subcommand("create")
    public void create(Player executor, String homeName) {
        Clan clan = getClanIfPresent(executor);
        if(clan == null) return;
        if(!hasPermission(executor, clan, ClanPermission.CREATE_HOME)) return;
        if(homeCache.getOwningClan(homeName) != null) {
            executor.sendMessage(messages.homes().homeWithNameAlreadyExists().parse("home", homeName));
            return;
        }
        ClanHome ch = ClanHome.builder().name(homeName)
                .displayName(Component.text(homeName, NamedTextColor.GRAY))
                .icon()



    }




}
