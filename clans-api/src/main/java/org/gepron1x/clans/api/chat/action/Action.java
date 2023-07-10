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
package org.gepron1x.clans.api.chat.action;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Optional;

public interface Action {

    Action EMPTY = (audience, resolver) -> {};

    void send(Audience audience, TagResolver resolver);

	default Optional<Component> text(TagResolver resolver) {
		return Optional.empty();
	}

	default void send(Audience audience) {
		send(audience, TagResolver.empty());
	}



    default void send(Audience audience, TagResolver... resolvers) {
        send(audience, TagResolver.resolver(resolvers));
    }

    interface NoResolver extends Action {
        void send(Audience audience);

        @Override
        default void send(Audience audience, TagResolver resolver) {
            send(audience);
        }
    }
}
