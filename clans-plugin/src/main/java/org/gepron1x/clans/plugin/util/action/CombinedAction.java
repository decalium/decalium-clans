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
package org.gepron1x.clans.plugin.util.action;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.chat.action.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class CombinedAction implements Action {

	private final Iterable<? extends Action> actions;

	public CombinedAction(Iterable<? extends Action> actions) {

		this.actions = actions;
	}

	@Override
	public void send(Audience audience, TagResolver resolver) {
		for (Action action : actions) action.send(audience, resolver);
	}

	@Override
	public Optional<Component> text(TagResolver resolver) {
		List<Component> list = new ArrayList<>();
		for (Action action : actions) action.text(resolver).ifPresent(list::add);
		if (list.size() == 0) return Optional.empty();
		if (list.size() == 1) return Optional.of(list.get(0));
		return Optional.of(Component.join(JoinConfiguration.newlines(), list));
	}

	public Iterable<? extends Action> actions() {
		return actions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CombinedAction that = (CombinedAction) o;
		return Objects.equals(actions, that.actions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(actions);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("actions", actions)
				.toString();
	}
}
