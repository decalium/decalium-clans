/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
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
package org.gepron1x.clans.api.util.player;
import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public interface PlayerReference extends ForwardingAudience.Single {


    static PlayerReference reference(Player player) {
        return new UuidPlayerReference(player.getServer(), player.getUniqueId());
    }

    boolean ifOnline(Consumer<Player> consumer);

    Optional<Player> player();

    default boolean online() {
        return player().isPresent();
    }

    default Player orElseThrow() {
        return player().orElseThrow();
    }

    PlayerProfile profile();



    @Override
    @NotNull default Audience audience() {
        return player().isEmpty() ? Audience.empty() : player().orElseThrow();
    }
}
