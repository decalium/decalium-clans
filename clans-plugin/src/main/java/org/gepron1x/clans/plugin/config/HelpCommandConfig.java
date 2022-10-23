/*
 * decalium-clans-rewrite
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans-rewrite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans-rewrite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans-rewrite. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.config;


import cloud.commandframework.minecraft.extras.MinecraftHelp;
import org.bukkit.command.CommandSender;
import org.gepron1x.clans.plugin.util.message.Message;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultObject;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface HelpCommandConfig {

    @ConfKey("colors")
    @DefaultObject("colorsDefault")
    MinecraftHelp.HelpColors colors();

    static MinecraftHelp.HelpColors colorsDefault() {
        return MinecraftHelp.DEFAULT_HELP_COLORS;
    }
    @ConfKey("messages")
    @SubSection Messages messages();

    interface Messages {

        @ConfKey("title")
        @DefaultString("Decalium Clans Help")
        Message title();

        @ConfKey("command")
        @DefaultString("Command")
        Message command();

        @ConfKey("description")
        @DefaultString("Description")
        Message description();

        @ConfKey("no-description")
        @DefaultString("No description")
        Message noDescription();

        @ConfKey("arguments")
        @DefaultString("Arguments")
        Message arguments();

        @ConfKey("optional")
        @DefaultString("Optional")
        Message optional();

        @ConfKey("search-results")
        @DefaultString("Showing search results for query")
        Message searchResults();

        @ConfKey("no-results-found")
        @DefaultString("No results for query")
        Message noResults();

        @ConfKey("available-commands")
        @DefaultString("Available Commands")
        Message availableCommands();

        @ConfKey("click-to-show-help")
        @DefaultString("Click to show help for this command")
        Message clickToShowHelp();

        @ConfKey("page-out-of-range")
        @DefaultString("Error: Page <page> is not in range. Must be in range [1, <max_pages>]")
        Message pageOutOfRange();

        @ConfKey("click-for-next-page")
        @DefaultString("Click for next page")
        Message clickForNextPage();

        @ConfKey("click-for-previous-page")
        @DefaultString("Click for previous page")
        Message clickForPreviousPage();

        default MinecraftHelp.MessageProvider<CommandSender> messageProvider() {
            Map<String, Message> map = new HashMap<>();
            map.put(MinecraftHelp.MESSAGE_HELP_TITLE, title());
            map.put(MinecraftHelp.MESSAGE_COMMAND, command());
            map.put(MinecraftHelp.MESSAGE_DESCRIPTION, description());
            map.put(MinecraftHelp.MESSAGE_NO_DESCRIPTION, noDescription());
            map.put(MinecraftHelp.MESSAGE_ARGUMENTS, arguments());
            map.put(MinecraftHelp.MESSAGE_OPTIONAL, optional());
            map.put(MinecraftHelp.MESSAGE_SHOWING_RESULTS_FOR_QUERY, searchResults());
            map.put(MinecraftHelp.MESSAGE_NO_RESULTS_FOR_QUERY, noResults());
            map.put(MinecraftHelp.MESSAGE_AVAILABLE_COMMANDS, availableCommands());
            map.put(MinecraftHelp.MESSAGE_CLICK_TO_SHOW_HELP, clickToShowHelp());
            map.put(MinecraftHelp.MESSAGE_PAGE_OUT_OF_RANGE, pageOutOfRange());
            map.put(MinecraftHelp.MESSAGE_CLICK_FOR_NEXT_PAGE, clickForNextPage());
            map.put(MinecraftHelp.MESSAGE_CLICK_FOR_PREVIOUS_PAGE, clickForPreviousPage());

            return (sender, key, args) -> Objects.requireNonNull(map.get(key)).asComponent();

        }



    }




}
