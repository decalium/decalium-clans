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
package org.gepron1x.clans.plugin.economy;

import net.milkbowl.vault.economy.Economy;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.plugin.config.settings.PricesConfig;
import org.gepron1x.clans.plugin.util.services.Services;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public record VaultHook(Users users, Services services, PricesConfig prices, FactoryOfTheFuture futuresFactory) {

    public Users hook() {
        return services.get(Economy.class).<Users>map(economy -> {
                    return new EconomyUsers(users, economy, prices, futuresFactory);
                }).orElse(users);
    }
}
