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
package org.gepron1x.clans.plugin.config.messages;

import org.gepron1x.clans.api.chat.action.Message;
import space.arim.dazzleconf.annote.ConfKey;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface LevelMessages {

    @DefaultString("<prefix> You already have a maximum level")
    @ConfKey("max-level")
	Message maxLevel();

    @DefaultString("<prefix> Too many members. Your clan has only <slots> slots. Upgrade the clan to get more")
    @ConfKey("too-many-members")
	Message tooManyMembers();

    @DefaultString("<prefix> Too many homes. Your clan can only have <homes> homes. Upgrade the clan to get more.")
    @ConfKey("too-many-homes")
	Message tooManyHomes();


    @DefaultString("<prefix> Successfully upgraded clan to level <level>.")
	Message upgraded();

}
