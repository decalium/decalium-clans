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
package org.gepron1x.clans.plugin.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.cache.ClanCache;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public final class ClansExpansion extends PlaceholderExpansion {

    private static final String TAG = "tag",
            DISPLAY_NAME = "display_name",
            OWNER = "owner", MEMBER_COUNT = "member_count",
            MEMBERS_ONLINE_COUNT = "member_online_count",
            MEMBER_ROLE = "member_role",
            LEVEL = "level";

    private static final String STATISTIC = "statistic_";


    private final Server server;
    private final ClansConfig clansConfig;
    private final ClanCache cache;
    private final LegacyComponentSerializer legacy;

    public ClansExpansion(@NotNull Server server, @NotNull ClansConfig clansConfig, @NotNull ClanCache cache, @NotNull LegacyComponentSerializer legacy) {
        this.server = server;
        this.clansConfig = clansConfig;
        this.cache = cache;
        this.legacy = legacy;
    }


    @Override
    public @NotNull String getIdentifier() {
        return "clans";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "gepron1x";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.1";
    }

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        IdentifiedDraftClan clan = cache.getUserClan(p.getUniqueId());
        if(clan == null) return legacy.serialize(clansConfig.noClanPlaceholder());
        Optional<ClanMember> member = clan.member(p);
        if(params.startsWith(STATISTIC)) {
            StatisticType type = StatisticType.registry().value(params.substring(STATISTIC.length()));
            if(type == null) return "Unknown statistic";
            return String.valueOf(clan.statisticOr(type, 0));
        }

        return switch(params) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 
            case TAG -> clan.tag();
            case DISPLAY_NAME -> legacy.serialize(clan.displayName());
            case OWNER -> legacy.serialize(clan.owner().renderName(server));
            case MEMBER_COUNT -> String.valueOf(clan.members().size());
            case MEMBER_ROLE -> member.map(ClanMember::role).map(ClanRole::displayName).map(this.legacy::serialize).orElse("");
            case LEVEL -> String.valueOf(clan.level());
            default -> null;
        };
    }



}
