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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.gepron1x.clans.plugin.util.message.Message;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
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
        if(!matcher.matches()) {
			return new ParsedAction(new MessageAction(Message.message(value, miniMessage)), value);
		}
        String type = matcher.group(1);
		String text = matcher.group(2);
        ActionArgs args = new ActionArgs(splitQuoted(text));
        return new ParsedAction(switch(type) {
			case "actionbar" -> new ActionBarAction(Message.message(text, miniMessage));
            case "sound" -> {
                Key key = args.requireArg(0).asKey();
                float volume = args.arg(1).map(ActionArgs.Arg::asFloat).orElse(1f);
                float pitch = args.arg(2).map(ActionArgs.Arg::asFloat).orElse(1f);
                yield new SoundAction(Sound.sound(key, Sound.Source.AMBIENT, volume, pitch));
            }
            case "title" -> {
                Message title = args.requireArg(0).asMessage(miniMessage);
                Message subTitle = args.arg(1).map(arg -> arg.asMessage(miniMessage)).orElse(Message.EMPTY);
                Duration fadeIn = args.arg(2).map(ActionArgs.Arg::asDuration).orElse(Title.DEFAULT_TIMES.fadeIn());
                Duration stay = args.arg(3).map(ActionArgs.Arg::asDuration).orElse(Title.DEFAULT_TIMES.stay());
                Duration fadeOut = args.arg(4).map(ActionArgs.Arg::asDuration).orElse(Title.DEFAULT_TIMES.fadeOut());
                yield new TitleAction(title, subTitle, Title.Times.times(fadeIn, stay, fadeOut));
            }
            default -> new MessageAction(Message.message(text, miniMessage));
        }, value);
    }

    public Action parse(Collection<String> values) {
        if(values.size() == 0) return Action.EMPTY;
        List<Action> actions = new ArrayList<>(values.size());
        for(String s : values) actions.add(parseSingle(s));
        if(actions.size() == 1) return actions.get(0);
        return new CombinedAction(actions);
    }

    private List<String> splitQuoted(String input) {
        List<String> tokens = new ArrayList<>();
        int startPosition = 0;
        boolean isInQuotes = false;
        for (int currentPosition = 0; currentPosition < input.length(); currentPosition++) {
            if (input.charAt(currentPosition) == '\"' || input.charAt(currentPosition) == '\'') {
                isInQuotes = !isInQuotes;
            }
            else if (input.charAt(currentPosition) == ';' && !isInQuotes) {
                tokens.add(input.substring(startPosition, currentPosition).strip());
                startPosition = currentPosition + 1;
            }
        }

        String lastToken = input.substring(startPosition);
        if (lastToken.equals(";")) {
            tokens.add("");
        } else {
            tokens.add(lastToken.strip());
        }
        return tokens;
    }

}
