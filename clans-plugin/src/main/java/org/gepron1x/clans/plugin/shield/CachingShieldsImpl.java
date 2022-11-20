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
import org.gepron1x.clans.api.shield.CachingShields;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.api.shield.Shields;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CachingShieldsImpl implements CachingShields {

    private final Shields shields;

    private final ConcurrentHashMap<String, Shield> cache = new ConcurrentHashMap<>();
    private final FactoryOfTheFuture futuresFactory;



    public CachingShieldsImpl(Shields shields, FactoryOfTheFuture futuresFactory) {

        this.shields = shields;
        this.futuresFactory = futuresFactory;
    }


    @Override
    public CentralisedFuture<Shield> add(Clan clan, Duration duration) {
        return shields.add(clan, duration).thenApply(s -> {
            cache.put(clan.tag(), s);
            return s;
        });
    }

    @Override
    public CentralisedFuture<?> delete(String tag) {
        return shields.delete(tag).thenAccept(ignored -> cache.remove(tag));
    }

    @Override
    public CentralisedFuture<Shield> currentShield(String tag) {
        Shield shield = cache.get(tag);
        if(shield != null) return futuresFactory.completedFuture(shield);
        return shields.currentShield(tag).thenApply(s -> {
            cache.put(tag, s);
            return s;
        });
    }

    @Override
    public CentralisedFuture<?> cleanExpired() {
        return shields.cleanExpired().thenAccept(ignored -> {
            cache.entrySet().removeIf(entry -> entry.getValue().expired());
        });
    }

    @Override
    public CentralisedFuture<Map<String, Shield>> shields() {
        return shields.shields();
    }

    @Override
    public Shield shield(String tag) {
        return cache.getOrDefault(tag, Shield.NONE);
    }
}
