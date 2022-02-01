package org.gepron1x.clans.plugin.editor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.editor.ClanEditor;
import org.gepron1x.clans.api.editor.HomeEditor;
import org.gepron1x.clans.api.editor.MemberEditor;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public final class PostClanEditor implements ClanEditor {
    private final Clan clan;
    private final RegionContainer regionContainer;

    public PostClanEditor(Clan clan, RegionContainer regionContainer) {
        this.clan = clan;
        this.regionContainer = regionContainer;
    }
    @Override
    public ClanEditor setDisplayName(@NotNull Component displayName) {
        return this;
    }

    @Override
    public ClanEditor setStatistic(@NotNull StatisticType type, int value) {
        return this;
    }

    @Override
    public ClanEditor incrementStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEditor removeStatistic(@NotNull StatisticType type) {
        return this;
    }

    @Override
    public ClanEditor addMember(@NotNull ClanMember member) {
        return this;
    }

    @Override
    public ClanEditor removeMember(@NotNull ClanMember member) {
        return this;
    }

    @Override
    public ClanEditor editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEditor> consumer) {
        return this;
    }

    @Override
    public ClanEditor addHome(@NotNull ClanHome home) {
        RegionManager regionManager = requireNonNull(
                regionContainer.get(BukkitAdapter.adapt(requireNonNull(home.getLocation().getWorld())))
        );
        ProtectedCuboidRegion region = createForHome(home);
        regionManager.addRegion(region);
        return this;
    }


    private ProtectedCuboidRegion createForHome(ClanHome home) {
        Location location = home.getLocation();
        double size = 30;
        double halfSize = size / 2;
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        BlockVector3 first = BlockVector3.at(x - halfSize, y - halfSize, z - halfSize);
        BlockVector3 second = BlockVector3.at(x + halfSize, y + halfSize, z + halfSize);
        return new ProtectedCuboidRegion(nameFor(clan, home), first, second);

    }

    @Override
    public ClanEditor removeHome(@NotNull ClanHome home) {
        RegionManager regionManager = getRegionManager(home.getLocation());
        regionManager.removeRegion(nameFor(clan, home));
        return this;
    }

    @Override
    public ClanEditor editHome(@NotNull String name, @NotNull Consumer<HomeEditor> consumer) {
        return this;
    }

    private RegionManager getRegionManager(Location location) {
        return requireNonNull(regionContainer.get(BukkitAdapter.adapt(requireNonNull(location.getWorld()))));

    }

    private static String nameFor(Clan clan, ClanHome home) {
        return "decaliumclans_"+clan.getTag()+"_"+home.getName();
    }
}
