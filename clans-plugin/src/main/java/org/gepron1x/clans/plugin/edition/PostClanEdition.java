package org.gepron1x.clans.plugin.edition;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.wg.HologramOfHome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class PostClanEdition implements ClanEdition {
    private static final NamespacedKey CLAN_NAME = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "clan_name");
    private static final NamespacedKey CLAN_HOME_NAME = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "clan_home_name");
    private final Clan clan;
    private final ClansConfig clansConfig;
    private final RegionContainer regionContainer;

    public PostClanEdition(Clan clan, ClansConfig clansConfig, RegionContainer regionContainer) {
        this.clan = clan;
        this.clansConfig = clansConfig;
        this.regionContainer = regionContainer;
    }
    @Override
    public ClanEdition rename(@NotNull Component displayName) {
        return this;
    }

    @Override
    public ClanEdition setStatistic(@NotNull StatisticType type, int value) {
        return this;
    }

    @Override
    public ClanEdition incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition removeStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
        for(ClanHome home : clan.homes()) {
            RegionManager regionManager =  getRegionManager(home.location());
            ProtectedRegion region = regionManager.getRegion(nameFor(clan, home));
            if(region == null) continue;
            region.getMembers().addPlayer(member.uniqueId());
        }
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        for(ClanHome home : clan.homes()) {
            RegionManager regionManager = getRegionManager(home.location());
            ProtectedRegion region = regionManager.getRegion(nameFor(clan, home));
            if(region == null) continue;
            region.getMembers().removePlayer(member.uniqueId());
        }
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        consumer.accept(new PostMemberEdition());
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        RegionManager regionManager = getRegionManager(home.location());
        ProtectedCuboidRegion region = createForHome(home);
        DefaultDomain members = region.getMembers();
        clan.memberMap().keySet().forEach(members::addPlayer);
        new HologramOfHome(this.clansConfig, this.clan, home).spawnIfNotPresent();
        regionManager.addRegion(region);

        return this;
    }


    private ProtectedCuboidRegion createForHome(ClanHome home) {
        Location location = home.location();
        double halfSize = this.clansConfig.homes().homeRegionRadius();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        BlockVector3 first = BlockVector3.at(x - halfSize, y - halfSize, z - halfSize);
        BlockVector3 second = BlockVector3.at(x + halfSize, y + halfSize, z + halfSize);
        return new ProtectedCuboidRegion(nameFor(clan, home), first, second);

    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
        RegionManager regionManager = getRegionManager(home.location());
        regionManager.removeRegion(nameFor(clan, home));
        new HologramOfHome(this.clansConfig, this.clan, home).destroy();
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        ClanHome home = clan.home(name).orElseThrow();
        RegionManager mgr = getRegionManager(home.location());
        consumer.accept(new PostHomeEdition(home, mgr));
        return this;
    }

    private RegionManager getRegionManager(Location location) {
        return requireNonNull(regionContainer.get(BukkitAdapter.adapt(requireNonNull(location.getWorld()))));

    }

    private static String nameFor(Clan clan, ClanHome home) {
        return "decaliumclans_"+clan.tag()+"_"+home.name();
    }

    private static class PostMemberEdition implements MemberEdition {


        @Override
        public MemberEdition appoint(@NotNull ClanRole role) {
            return this;
        }
    }

    private class PostHomeEdition implements HomeEdition {

        private final ClanHome home;
        private final RegionManager regionManager;
        private PostHomeEdition(ClanHome home, RegionManager regionManager) {
            this.home = home;
            this.regionManager = regionManager;
        }


        @Override
        public HomeEdition setIcon(@Nullable ItemStack icon) {
            new HologramOfHome(PostClanEdition.this.clansConfig, PostClanEdition.this.clan, this.home).icon(icon);
            return this;
        }

        @Override
        public HomeEdition move(@NotNull Location location) {
            return this;
        }

        @Override
        public HomeEdition rename(@NotNull Component displayName) {
            new HologramOfHome(PostClanEdition.this.clansConfig, PostClanEdition.this.clan, this.home).rename(displayName);
            return this;
        }
    }
}
