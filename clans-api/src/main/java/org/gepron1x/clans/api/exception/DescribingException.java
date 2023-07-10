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
package org.gepron1x.clans.api.exception;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.chat.action.Action;

import java.util.Optional;

public class DescribingException extends RuntimeException implements Action {

    private final Action action;

    public DescribingException(ComponentLike description) {
        this((a, r) -> a.sendMessage(description));
    }


	public DescribingException(Action action) {
		this.action = action;
	}

	@Override
	public void send(Audience audience, TagResolver resolver) {
		action.send(audience, resolver);
	}

	@Override
	public Optional<Component> text(TagResolver resolver) {
		return action.text(resolver);
	}
}
