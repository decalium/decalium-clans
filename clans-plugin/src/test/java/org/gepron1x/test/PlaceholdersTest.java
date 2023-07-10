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


import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholdersTest {
	private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");

	public static void main(String[] args) {
		System.out.println(new PapiPreprocessor().apply("%test%"));
	}

	public static final class PapiPreprocessor implements UnaryOperator<String> {
		@Override
		public String apply(String s) {
			Matcher matcher = PLACEHOLDER_PATTERN.matcher(s);
			StringBuilder builder = new StringBuilder();
			while (matcher.find()) {
				String match = matcher.group();
				matcher.appendReplacement(builder, placeholder(match));
			}
			matcher.appendTail(builder);
			return builder.toString();
		}


		private String placeholder(String placeholder) {
			return "<papi:'" + placeholder.substring(1, placeholder.length() - 1) + "'>";
		}
	}
}
