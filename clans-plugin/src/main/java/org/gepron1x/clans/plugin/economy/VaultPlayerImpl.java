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

import com.google.common.base.MoreObjects;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.Objects;

public final class VaultPlayerImpl implements VaultPlayer {
	private final Player player;
	private final Economy economy;

	public VaultPlayerImpl(Player player, Economy economy) {

		this.player = player;
		this.economy = economy;
	}

	@Override
	public double balance() {
		return economy.getBalance(player);
	}

	@Override
	public boolean has(double amount) {
		if (player.hasPermission("clans.economy.ignore")) return true;
		return economy.has(player, amount);
	}

	@Override
	public void withdraw(double amount) {
		if (player.hasPermission("clans.economy.ignore")) return;
		economy.withdrawPlayer(player, amount);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VaultPlayerImpl that = (VaultPlayerImpl) o;
		return player.equals(that.player);
	}

	@Override
	public int hashCode() {
		return Objects.hash(player);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("player", player)
				.toString();
	}
}
