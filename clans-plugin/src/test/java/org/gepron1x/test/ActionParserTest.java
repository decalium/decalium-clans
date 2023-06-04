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
package org.gepron1x.test;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gepron1x.clans.plugin.util.action.ActionParser;

import java.util.List;

public class ActionParserTest {

	public static void main(String[] args) {
		ActionParser parser = new ActionParser(MiniMessage.miniMessage());
		var values = List.of("<red>Regular message",
				"[!!]]2][[]Regular message",
				"[title] \"[Pull; up]\"; Nigga; 1; 2; 3",
				"[actionbar] I OWN SWAG;;;",
				"[sound] minecraft:ambient.cave",
				"[sound] minecraft:ambient.cave; 1; 2"
		);
		System.out.println(parser.parse(values));
	}
}
