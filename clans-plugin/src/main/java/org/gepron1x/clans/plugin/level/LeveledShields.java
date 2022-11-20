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
package org.gepron1x.clans.plugin.level;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.exception.DescribingException;
import org.gepron1x.clans.api.shield.Shield;
import org.gepron1x.clans.api.shield.Shields;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.time.Duration;
import java.util.Map;

public final class LeveledShields implements Shields {

    private final Shields shields;
    private final ClansConfig config;
    private final MessagesConfig messages;
    private final FactoryOfTheFuture futuresFactory;

    public LeveledShields(Shields shields, ClansConfig config, MessagesConfig messages, FactoryOfTheFuture futuresFactory) {
        this.shields = shields;
        this.config = config;
        this.messages = messages;
        this.futuresFactory = futuresFactory;
    }

    @Override
    public CentralisedFuture<Shield> add(Clan clan, Duration duration) {
        if(clan.level() < config.levels().allowAt().shields()) {
            return futuresFactory.failedFuture(new DescribingException(messages.level().cannotCreateShield().with("level", config.levels().allowAt().shields())));
        }
        return shields.add(clan, duration);
    }

    @Override
    public CentralisedFuture<?> delete(String tag) {
        return shields.delete(tag);
    }

    @Override
    public CentralisedFuture<Shield> currentShield(String tag) {
        return shields.currentShield(tag);
    }

    @Override
    public CentralisedFuture<?> cleanExpired() {
        return shields.cleanExpired();
    }

    @Override
    public CentralisedFuture<Map<String, Shield>> shields() {
        return shields.shields();
    }
}

