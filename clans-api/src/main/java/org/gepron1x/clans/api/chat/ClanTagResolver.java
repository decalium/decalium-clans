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
package org.gepron1x.clans.api.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public record ClanTagResolver(@NotNull DraftClan clan) implements TagResolver {

    public static ClanTagResolver clan(@NotNull DraftClan clan) {
        return new ClanTagResolver(clan);
    }

    public static TagResolver prefixed(@NotNull DraftClan clan, String prefix) {
        return PrefixedTagResolver.prefixed(clan(clan), prefix);
    }

    public static TagResolver prefixed(@NotNull DraftClan clan) {
        return prefixed(clan, "clan");
    }

    private static final String TAG = "tag";
    private static final String DISPLAY_NAME = "display_name";
    private static final String MEMBERS_SIZE = "members_size";
    private static final String HOMES_SIZE = "homes_size";

    private static final String STATISTIC = "statistic_";
    private static final String MEMBER = "member";
    private static final String MEMBERS = MEMBER + "s";
    private static final String OWNER = "owner_";

    private static final Set<String> KEYS = Set.of(TAG, DISPLAY_NAME, MEMBERS_SIZE, HOMES_SIZE, STATISTIC, MEMBER, MEMBERS, OWNER);


    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        Component component = switch (name) {
            case TAG -> Component.text(clan.tag());
            case DISPLAY_NAME -> clan.displayName();
            case MEMBERS_SIZE -> Component.text(clan.members().size());
            case HOMES_SIZE -> Component.text(clan.homes().size());
            case MEMBERS -> clan.members().stream().map(member -> member.renderName(Bukkit.getServer())).collect(Component.toComponent(Component.newline()));
            default -> null;
        };

        if(name.equals(MEMBER)) {
            String playerName = arguments.popOr("Player name required.").value();
            String tag = arguments.popOr("Value type required").value();
            return Optional.ofNullable(Bukkit.getOfflinePlayerIfCached(playerName))
                    .flatMap(clan::member).map(ClanMemberTagResolver::new)
                    .map(resolver -> resolver.resolve(tag))
                    .orElse(null);
        }

        if(component != null) return Tag.selfClosingInserting(component);

        if(name.startsWith(STATISTIC)) {
            StatisticType type = new StatisticType(name.substring(STATISTIC.length()));
            int value = clan.statisticOr(type, 0);
            return Tag.inserting(Component.text(value));
        }

        if(name.startsWith(OWNER)) {
            ClanMember member = clan.owner();
            return PrefixedTagResolver.prefixed(new ClanMemberTagResolver(member), "owner").resolve(name, arguments, ctx);
        }
        return null;
    }

    @Override
    public boolean has(@NotNull String name) {
        for(String key : KEYS) {
            if(name.startsWith(key)) return true;
        }
        return false;
    }
}
