package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.gepron1x.clans.api.region.ClanRegion;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public record WgRegionSet(RegionContainer container, Iterable<ClanRegion> regions) {

	public void addMember(UUID member) {
		forEach(region -> region.getMembers().addPlayer(member));
	}

	public void addMembers(Iterable<UUID> members) {
		forEach(region -> {
			members.forEach(region.getMembers()::addPlayer);
		});
	}

	public void removeMember(UUID member) {
		forEach(region -> region.getMembers().removePlayer(member));
	}

	public void clear() {
		for (ClanRegion region : regions) {
			RegionManager manager = container.get(BukkitAdapter.adapt(region.location().getWorld()));
			if (manager != null) manager.removeRegion("clans_region_" + region.id());
		}
	}

	private void forEach(Consumer<ProtectedRegion> consumer) {
		for (ClanRegion region : regions) {
			new ProtectedRegionOf(container, region).region().ifPresent(consumer);
		}
	}

	public Collection<ProtectedRegion> protectedRegions() {
		HashSet<ProtectedRegion> result = new HashSet<>();
		for (ClanRegion region : regions) {
			new ProtectedRegionOf(container, region).region().ifPresent(result::add);
		}
		return result;
	}
}
