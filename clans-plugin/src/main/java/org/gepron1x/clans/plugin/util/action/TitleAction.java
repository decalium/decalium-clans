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
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import org.gepron1x.clans.plugin.util.message.Message;

import java.util.Objects;

public final class TitleAction implements Action {

    private final Message title;
    private final Message subTitle;
    private final Title.Times times;

    public TitleAction(Message title, Message subTitle, Title.Times times) {
        this.title = title;
        this.subTitle = subTitle;
        this.times = times;
    }

    @Override
    public void send(Audience audience, TagResolver resolver) {
        audience.showTitle(Title.title(title.with(resolver).asComponent(), subTitle.with(resolver).asComponent(), times));
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TitleAction that = (TitleAction) o;
		return Objects.equals(title, that.title) && Objects.equals(subTitle, that.subTitle) && Objects.equals(times, that.times);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, subTitle, times);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("title", title)
				.add("subTitle", subTitle)
				.add("times", times)
				.toString();
	}
}
