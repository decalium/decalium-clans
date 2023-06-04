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
package org.gepron1x.clans.plugin.edition;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.api.edition.EmptyClanEdition;
import org.gepron1x.clans.api.edition.home.HomeEdition;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.wg.HologramOfHome;
import org.gepron1x.clans.plugin.wg.WgRegionSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public final class PostClanEdition implements EmptyClanEdition {
    private final Clan clan;
    private final ClansConfig clansConfig;
	private final WgRegionSet regionSet;

	public PostClanEdition(Clan clan, ClansConfig clansConfig, WgRegionSet regionSet) {
        this.clan = clan;
        this.clansConfig = clansConfig;
		this.regionSet = regionSet;
	}

    @Override
    public ClanEdition addMember(@NotNull ClanMember member) {
		regionSet.addMember(member.uniqueId());
        return this;
    }

    @Override
    public ClanEdition removeMember(@NotNull ClanMember member) {
        regionSet.removeMember(member.uniqueId());
        return this;
    }

    @Override
    public ClanEdition editMember(@NotNull UUID uuid, @NotNull Consumer<MemberEdition> consumer) {
        consumer.accept(new PostMemberEdition());
        return this;
    }

    @Override
    public ClanEdition addHome(@NotNull ClanHome home) {
        new HologramOfHome(this.clansConfig, this.clan, home).spawnIfNotPresent();
        return this;
    }

    @Override
    public ClanEdition removeHome(@NotNull ClanHome home) {
		new HologramOfHome(this.clansConfig, clan, home).destroy();
        return this;
    }

    @Override
    public ClanEdition editHome(@NotNull String name, @NotNull Consumer<HomeEdition> consumer) {
        ClanHome home = clan.home(name).orElseThrow();
        consumer.accept(new PostHomeEdition(home));
        return this;
    }

    private static class PostMemberEdition implements MemberEdition {


        @Override
        public MemberEdition appoint(@NotNull ClanRole role) {
            return this;
        }
    }

    private class PostHomeEdition implements HomeEdition {

        private final ClanHome home;
        private PostHomeEdition(ClanHome home) {
            this.home = home;
        }


        @Override
        public HomeEdition setIcon(@Nullable ItemStack icon) {
            new HologramOfHome(PostClanEdition.this.clansConfig, PostClanEdition.this.clan, this.home).icon(icon);
            return this;
        }

        @Override
        public HomeEdition move(@NotNull Location location) {
            return this;
        }

        @Override
        public HomeEdition rename(@NotNull Component displayName) {
            new HologramOfHome(PostClanEdition.this.clansConfig, PostClanEdition.this.clan, this.home).rename(displayName);
            return this;
        }

        @Override
        public HomeEdition upgrade() {
            new HologramOfHome(clansConfig, clan, home).rename(home.displayName());
            return this;
        }

        @Override
        public HomeEdition downgrade() {
            return upgrade();
        }
    }
}
