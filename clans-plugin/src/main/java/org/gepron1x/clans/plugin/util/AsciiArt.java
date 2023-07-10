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
package org.gepron1x.clans.plugin.util;

import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.slf4j.Logger;

public final class AsciiArt {


	private static final String ART = """
			________                           .__   .__                \s
			\\______ \\    ____    ____  _____   |  |  |__| __ __   _____ \s
			 |    |  \\ _/ __ \\ _/ ___\\ \\__  \\  |  |  |  ||  |  \\ /     \\\s
			 |    `   \\\\  ___/ \\  \\___  / __ \\_|  |__|  ||  |  /|  Y Y  \\
			/_______  / \\___  > \\___  >(____  /|____/|__||____/ |__|_|  /
			        \\/      \\/      \\/      \\/                        \\/\s
			_________  .__                                              \s
			\\_   ___ \\ |  |  _____     ____    ______                   \s
			/    \\  \\/ |  |  \\__  \\   /    \\  /  ___/                   \s
			\\     \\____|  |__ / __ \\_|   |  \\ \\___ \\                    \s
			 \\______  /|____/(____  /|___|  //____  >                   \s
			        \\/            \\/      \\/      \\/\s
			,_,_,_,_,_,_,_,_,_,_|___________________________________________________
			| | | | | | | | | | |__________________________________________________/
			'-'-'-'-'-'-'-'-'-'-|-------------------------------------------------
			""";


	private final Logger logger;

	public AsciiArt(Logger logger) {

		this.logger = logger;
	}


	public void print() {

		for (String str : ART.split("\n")) {
			this.logger.info(str);
		}
		this.logger.info("Version: " + DecaliumClansPlugin.VERSION);

	}
}
