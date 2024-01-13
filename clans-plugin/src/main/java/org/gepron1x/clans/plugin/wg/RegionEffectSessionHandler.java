package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.gepron1x.clans.api.region.ClanRegion;
import org.gepron1x.clans.api.region.effect.ActiveEffect;
import org.gepron1x.clans.plugin.MutableClansApi;

import java.util.Optional;
import java.util.Set;

public final class RegionEffectSessionHandler extends Handler {


	private final MutableClansApi api;

	public static class Factory extends Handler.Factory<RegionEffectSessionHandler> {


		private final MutableClansApi api;

		public Factory(MutableClansApi api) {

			this.api = api;
		}
		@Override
		public RegionEffectSessionHandler create(Session session) {
			return new RegionEffectSessionHandler(session, api);
		}
	}
	/**
	 * Create a new handler.
	 *
	 * @param session The session
	 */
	private RegionEffectSessionHandler(Session session, MutableClansApi api) {
		super(session);
		this.api = api;
	}


	@Override
	public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
								   Set<ProtectedRegion> entered,
								   Set<ProtectedRegion> exited,
								   MoveType moveType) {

		for(ProtectedRegion region : entered) {
			region(player, region).ifPresent(effect -> {
				effect.effect().onEnter(Bukkit.getPlayer(player.getUniqueId()), effect.left());
			});
		}

		for(ProtectedRegion region : exited) {
			region(player, region).ifPresent(effect -> {
				effect.effect().onLeave(Bukkit.getPlayer(player.getUniqueId()));
			});
		}
		return true;
	}

	private Optional<ActiveEffect> region(LocalPlayer player, ProtectedRegion region) {
		if(!region.isMember(player)) return Optional.empty();
		return Optional.ofNullable(region.getFlag(WgExtension.REGION_ID)).flatMap(api.regions()::region)
				.flatMap(ClanRegion::activeEffect);
	}
}
