/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.shield;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.api.shield.Shields;
import org.gepron1x.clans.plugin.storage.ShieldStorage;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public final class ShieldsImpl implements Shields {

    private final ShieldStorage storage;
    private final FactoryOfTheFuture futuresFactory;

    public ShieldsImpl(ShieldStorage storage, FactoryOfTheFuture futuresFactory) {

        this.storage = storage;
        this.futuresFactory = futuresFactory;
    }
    @Override
    public CentralisedFuture<Shield> add(Clan clan, Duration duration) {
        final Instant now = Instant.now();
        return futuresFactory.supplyAsync(() -> {
            Shield shield = new ShieldImpl(now, now.plus(duration));
            this.storage.add(clan.id(), shield);
            return shield;
        });
    }

    @Override
    public CentralisedFuture<?> delete(String tag) {
        return futuresFactory.runAsync(() -> storage.remove(tag));
    }

    @Override
    public CentralisedFuture<Shield> currentShield(String tag) {
        return futuresFactory.supplyAsync(() -> storage.get(tag));
    }

    @Override
    public CentralisedFuture<?> cleanExpired() {
        return futuresFactory.runAsync(storage::cleanExpired);
    }

    @Override
    public CentralisedFuture<Map<String, Shield>> shields() {
        return futuresFactory.supplyAsync(storage::activeShields);
    }
}
