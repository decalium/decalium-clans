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

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.chat.action.Message;
import org.gepron1x.clans.plugin.util.message.TextMessage;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

@ConfHeader({"Messages configuration. Placeholders are based on message context.",
		"Plugin uses minimessage format, legacy color codes (&a, &b, &c) WILL NOT WORK.",
		"See https://docs.adventure.kyori.net/minimessage/format.html to get understanding."})
public interface MessagesConfig {

	@ConfComments("Help command options.")
	@SubSection HelpCommandConfig help();

	@SubSection LevelMessages level();

	@ConfKey("prefix")
	@DefaultString("<aqua>DecaliumClans |")
	Component prefix();


	@ConfKey("no-permission")
	@DefaultString("<prefix> You do not have permission to use this.")
	Message noPermission();

	@ConfKey("no-online-players-in-clan")
	@DefaultString("<prefix> No online players in the clan.")
	Message noOnlinePlayers();

	@ConfKey("cannot-do-action-on-yourself")
	@DefaultString("<prefix> You can't do that with yourself!")
	Message cannotDoActionOnYourSelf();


	@ConfKey("no-clan-permission")
	@DefaultString("<prefix> Your role does not grant you permission to do this.")
	Message noClanPermission();

	@ConfKey("already-in-clan")
	@DefaultString("<prefix> You are already in a clan!")
	Message alreadyInClan();

	@ConfKey("player-is-already-in-clan")
	@DefaultString("<prefix> Player <player> is already in a clan!")
	Message playerIsAlreadyInClan();

	@ConfKey("not-in-clan")
	@DefaultString("<prefix> You are not in the clan!")
	Message notInTheClan();

	@SubSection
	@ConfKey("announcements")
	Announcements announcements();

	@SubSection
	@ConfKey("war")
	War war();

	@ConfKey("region")
	@SubSection Region region();


	@SubSection
	@ConfKey("commands")
	Commands commands();


	interface Announcements {
		@ConfKey("member-added")
		@DefaultString("<prefix> <member> joined the clan!")
		Message memberAdded();

		@ConfKey("member-removed")
		@DefaultString("<prefix> <member> is not with us anymore!")
		Message memberRemoved();

		@ConfKey("member-promoted")
		@DefaultString("<prefix> <member> is <role> now!")
		Message memberPromoted();

		@ConfKey("clan-deleted")
		@DefaultString("<prefix> Your clan is disbanded.")
		Message clanDeleted();

		@ConfKey("clan-set-display-name")
		@DefaultString("<prefix> Clan is now called <name>")
		Message clanSetDisplayName();

		@ConfKey("home-created")
		@DefaultString("<prefix> <member> created a new clan home <home_name>")
		Message homeCreated();

		@ConfKey("home-deleted")
		@DefaultString("<prefix> <member> deleted home <home_name>")
		Message homeDeleted();

		@ConfKey("home-upgraded")
		@DefaultString("<prefix> Home <home> is level <level> now!")
		Message homeUpgraded();

		@ConfKey("clan-owner-changed")
		@DefaultString("<prefix> <member> is the owner now.")
		Message clanOwnerChanged();


	}

	interface War {

		@ConfKey("player-died")
		@DefaultString("<prefix> ClanWar -> <member> died!")
		Message playerDied();

		@ConfKey("player-died-subtitle")
		@DefaultString("<gray><killer> \uD83D\uDDE1<red>\uD83C\uDF27</red> <victim></gray>")
		TextMessage playerDiedSubTitle();

		@ConfKey("win")
		@DefaultString("<prefix> ClanWar -> <clan_display_name> wins! Congratulations!")
		Message win();

		@ConfKey("preparation-title")
		@DefaultString("<first_display_name> vs <second_display_name>")
		TextMessage preparationTitle();

		@ConfKey("boss-bar-format")
		@DefaultString("<display_name> <alive>/<members>")
		TextMessage bossBarFormat();

		@ConfKey("navigation-bar-format")
		@DefaultString("Distance to <target>: <distance> <arrow>")
		TextMessage navigationBarFormat();

		@ConfKey("navigation-bar-format-in-different-world")
		@DefaultString("<target> is in <world> world")
		Message navigationDifferentWorld();
	}

	interface Region {

		@ConfKey("region-overlaps")
		@DefaultString("[title] <red>❌ Это место уже занято! ❌;Попробуйте поставить блок подальше")
		Message regionOverlaps();

		@ConfKey("not-in-clan")
		@DefaultStrings({"[sound] minecraft:note_block.iron_xylophone", "[title]<red>❌ Вы не состите в клане! ❌;Вступите в клан или создайте его."})
		Message notInClan();
	}


	interface Commands {
		@ConfKey("invalid-syntax")
		@DefaultString("<prefix> Invalid command syntax! Use /clan help to get information on how to use this command.")
		Message invalidSyntax();

		@ConfKey("invalid-argument")
		@DefaultString("<prefix> Invalid argument! <message>")
		Message invalidArgument();

		@ConfKey("command-for-players-only")
		@DefaultString("<prefix> Only players can execute this command!")
		Message onlyPlayersCanDoThis();

		@ConfKey("info-format")
		@DefaultString("Clan <clan_display_name> (<clan_tag>)<br>Owner: <clan_owner_name><br>Members: <members>")
		Message infoFormat();


		@ConfKey("display-name-set")
		@DefaultString("<prefix> Display name was set to <name>.")
		Message displayNameSet();

		@ConfKey("left-clan")
		@DefaultString("<prefix> Successfully left the clan.")
		Message left();

		@ConfKey("owner-cannot-leave")
		@DefaultString("<prefix> You cannot leave the clan because you are the owner.")
		Message ownerCannotLeave();

		@ConfKey("creation")
		@SubSection
		Creation creation();

		@ConfKey("deletion")
		@SubSection
		Deletion deletion();

		@ConfKey("invitation")
		@SubSection
		Invitation invitation();

		@ConfKey("member")
		@SubSection
		Member member();

		@ConfKey("home")
		@SubSection
		Home home();

		@ConfKey("war")
		@SubSection
		WarRequest wars();

		interface Creation {

			@ConfKey("clan-with-tag-already-exists")
			@DefaultString("<prefix> Clan with given tag already exists! Think about something more original!")
			Message clanWithTagAlreadyExists();

			@ConfKey("invalid-tag")
			@DefaultString("<prefix> Error! Invalid tag/name. you can only use english letters and numbers. [a-z0-9]. The display name also should containt at least 3 english letters.")
			Message invalidTag();

			@ConfKey("success")
			@DefaultString("<prefix> Clan <name> created successfully")
			Message success();

		}

		interface Deletion {

			@ConfKey("success")
			@DefaultString("<prefix> Clan deleted successfully.")
			Message success();
		}


		interface Invitation {

			@ConfKey("invitation")
			@DefaultString("<prefix> <sender> invites you to the clan <clan_display_name>! " +
					"<click:run_command:'/clan accept <sender>'>Click here</click> to accept invitation or type /clan accept <sender>")
			Message invitationMessage();

			@ConfKey("sent")
			@DefaultString("<prefix> Invitation successfully sent to <receiver>.")
			Message invitationSent();

			@ConfKey("player-accepted")
			@DefaultString("<prefix> <receiver> accepted your invitation.")
			Message playerAccepted();

			@ConfKey("accepted")
			@DefaultString("<prefix> You accepted invitation to <clan_display_name> from <sender>.")
			Message accepted();

			@ConfKey("player-declined")
			@DefaultString("<prefix> <receiver> declined your invitation.")
			Message playerDeclined();

			@ConfKey("declined")
			@DefaultString("<prefix> You declined invitation from <sender>.")
			Message declined();


			@ConfKey("no-invitations")
			@DefaultString("<prefix> You have no invitations from <player>")
			Message noInvitations();


			@ConfKey("clan-got-deleted")
			@DefaultString("<prefix> Clan you joined got deleted!")
			Message clanGotDeleted();

		}


		interface Member {

			@ConfKey("member-not-in-clan")
			@DefaultString("<prefix> Specified player is not a member of the clan.")
			Message notAMember();

			@ConfKey("member-has-higher-weight")
			@DefaultString("<prefix> Member <member> has higher weight than you. You cannot do any sanctions on him.")
			Message memberHasHigherWeight();

			@ConfKey("only-owner-can-do-this")
			@DefaultString("<prefix> Only owner can do this!")
			Message onlyOwnerCanDoThis();


			@ConfKey("role")
			@SubSection Role role();

			@ConfKey("kick")
			@SubSection Kick kick();


			interface Role {

				@ConfKey("success")
				@DefaultString("<prefix> Role was set successfully.")
				Message success();

				@ConfKey("role-has-higher-weight")
				@DefaultString("<prefix> Role <role> has higher weight than yours. You cannot set it.")
				Message roleHasHigherWeight();

				@ConfKey("role-not-found")
				@DefaultString("Role <role> was not found.")
				Message roleNotFound();

			}

			interface Kick {

				@ConfKey("success")
				@DefaultString("<prefix> Kicked <member> from clan successfully.")
				Message success();

				@ConfKey("kicked")
				@DefaultString("<prefix> <member> kicked you from the <clan>!")
				Message kicked();

			}

		}


		interface Home {

			@ConfKey("too-many-homes")
			@DefaultString("<prefix> Too many homes already exist!")
			Message tooManyHomes();


			@ConfKey("home-not-found")
			@DefaultString("<prefix> No home with name <name> found.")
			Message homeNotFound();

			@ConfKey("invalid-name")
			@DefaultString("<prefix> Error! Invalid home name. you can only use english letters and numbers. [a-z0-9]")
			Message invalidHomeName();

			@ConfKey("home-already-exists")
			@DefaultString("<prefix> Home with name <name> already exists!")
			Message homeAlreadyExists();

			@ConfKey("created")
			@DefaultString("<prefix> Created new home successfully.")
			Message created();

			@ConfKey("deleted")
			@DefaultString("<prefix> Deleted home succesfully.")
			Message deleted();

			@ConfKey("teleported")
			@DefaultString("<prefix> Teleported to <home>")
			Message teleported();

			@ConfKey("renamed")
			@DefaultString("<prefix> Successfully renamed the clan home")
			Message renamed();

			@ConfKey("upgraded")
			@DefaultString("<prefix> Successfully upgraded home to level <level>.")
			Message upgraded();


		}

		interface WarRequest {
			@ConfKey("request-message")
			@DefaultString("<prefix> <clan_display_name> Invites you to the clan war!")
			Message requestMessage();

			@ConfKey("accepter-message")
			@DefaultString("<prefix> <click:run_command:'/clan war accept <tag>'><red>Click here</red></click> to accept the request" +
					"         or run /clan war accept <tag>")
			Message acceptMessage();

			@ConfKey("no-requests")
			@DefaultString("<prefix> No requests from <tag>.")
			Message noRequests();

			@ConfKey("request-sent")
			@DefaultString("<prefix> War request sent.")
			Message requestSent();

			@ConfKey("declined-message")
			@DefaultString("<prefix> Denied war request from <clan_display_name>.")
			Message declined();

			@ConfKey("accepted-message")
			@DefaultString("<prefix> Accepted war request from <clan_display_name>.")
			Message accepted();

			@ConfKey("victim-declined")
			@DefaultString("<prefix> <clan_display_name> declined war request.")
			Message victimDeclined();

			@ConfKey("victim-accepted")
			@DefaultString("<prefix> <clan_display_name> accepted war request.")
			Message victimAccepted();
		}


	}

}
