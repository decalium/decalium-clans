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

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gepron1x.clans.plugin.util.message.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ActionParser {

    private static final Pattern ACTION_PATTERN = Pattern.compile("\\[([a-zA-Z\\d_]+)](.+)");

    private final MiniMessage miniMessage;

    public ActionParser(MiniMessage miniMessage) {

        this.miniMessage = miniMessage;
    }


    public Action parseSingle(String value) {
        Matcher matcher = ACTION_PATTERN.matcher(value);
        if(!matcher.matches()) throw new IllegalArgumentException("Invalid action format");
        String type = matcher.group(0);
        List<String> args = splitQuoted(matcher.group(1));
        return switch(type) {
            case "message" -> new MessageAction(Message.message(args.get(0), miniMessage));
        };
    }

    private List<String> splitQuoted(String input) {
        List<String> tokens = new ArrayList<>();
        int startPosition = 0;
        boolean isInQuotes = false;
        for (int currentPosition = 0; currentPosition < input.length(); currentPosition++) {
            if (input.charAt(currentPosition) == '\"') {
                isInQuotes = !isInQuotes;
            }
            else if (input.charAt(currentPosition) == ',' && !isInQuotes) {
                tokens.add(input.substring(startPosition, currentPosition));
                startPosition = currentPosition + 1;
            }
        }

        String lastToken = input.substring(startPosition);
        if (lastToken.equals(",")) {
            tokens.add("");
        } else {
            tokens.add(lastToken);
        }
        return tokens;
    }

}
