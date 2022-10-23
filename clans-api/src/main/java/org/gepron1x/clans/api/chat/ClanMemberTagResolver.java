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
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ClanMemberTagResolver(@NotNull ClanMember member) implements TagResolver.WithoutArguments {
    private static final String ROLE = "role";
    private static final String NAME = "name";
    private static final String UUID = "uuid";

    public static ClanMemberTagResolver clanMember(@NotNull ClanMember member) {
        return new ClanMemberTagResolver(member);
    }
    @Override
    public @Nullable Tag resolve(@NotNull String name) {

        Component component = switch (name) {
            case ROLE -> member.role().asComponent();
            case NAME -> member.asComponent();
            case UUID -> Component.text(member.uniqueId().toString());
            default -> null;
        };
        return component == null ? null : Tag.selfClosingInserting(component);
    }
}
