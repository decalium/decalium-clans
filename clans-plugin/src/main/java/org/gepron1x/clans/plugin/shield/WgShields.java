/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.shield;

import com.sk89q.worldguard.WorldGuard;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.api.shield.Shields;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.wg.WgExtension;
import org.gepron1x.clans.plugin.wg.WgHome;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.time.Duration;
import java.util.Map;

public final class WgShields implements Shields {

    private final Shields shields;
    private final WorldGuard worldGuard;
    private final ClansConfig config;

    public WgShields(Shields shields, WorldGuard worldGuard, ClansConfig config) {

        this.shields = shields;
        this.worldGuard = worldGuard;
        this.config = config;
    }
    @Override
    public CentralisedFuture<Shield> add(Clan clan, Duration duration) {
        return this.shields.add(clan, duration).thenApplySync(s -> {
            for(ClanHome home : clan.homes()) {
                new WgHome(worldGuard, clan, home).region().ifPresent(r -> {
                    r.setFlag(WgExtension.SHIELD_ACTIVE, true);
                    config.shields().shieldFlags().apply(r);
                });
            }
            return s;
        });
    }

    @Override
    public CentralisedFuture<?> delete(String tag) {
        return this.shields.delete(tag);
    }

    @Override
    public CentralisedFuture<Shield> currentShield(String tag) {
        return this.shields.currentShield(tag);
    }

    @Override
    public CentralisedFuture<?> cleanExpired() {
        return this.shields.cleanExpired();
    }

    @Override
    public CentralisedFuture<Map<String, Shield>> shields() {
        return this.shields.shields();
    }
}
