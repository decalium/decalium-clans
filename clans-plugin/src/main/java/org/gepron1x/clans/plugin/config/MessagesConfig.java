package org.gepron1x.clans.plugin.config;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.plugin.util.message.Message;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;

public interface MessagesConfig {


    @ConfKey("prefix")
    @DefaultString("<aqua>DecaliumClans |")
    Component prefix();


    @ConfKey("no-permission")
    @DefaultString("<prefix> You do not have permission to use this.")
    Message noPermission();


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
    @ConfKey("commands")
    Commands commands();

    interface Chat {

        @DefaultString("Clan chat/<role>")
        Message messageFormat();
    }




    interface Commands {

        @ConfKey("display-name-set")
        @DefaultString("<prefix> Display name was set to <name>.")
        Message displayNameSet();

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

        interface Creation {

            @ConfKey("clan-with-tag-already-exists")
            @DefaultString("<prefix> Clan with given tag already exists! Think about something more original!")
            Message clanWithTagAlreadyExists();

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
                    "<click:run_command:'clan accept <sender>'>Click here</click> to accept invitation or type /clan accept <sender>")
            Message invitationMessage();

            @ConfKey("sent")
            @DefaultString("<prefix> Invitation successfully sent to <receiver>.")
            Message invitationSent();

            @ConfKey("player-accepted")
            @DefaultString("<prefix> <reciever> accepted your invitation.")
            Message playerAccepted();

            @ConfKey("accepted")
            @DefaultString("<prefix> You accepted invitation to <clan_display_name> from <sender>.")
            Message accepted();

            @ConfKey("player-declined")
            @DefaultString("<prefix> <reciever> declined your invitation.")
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


            @ConfKey("home-not-found")
            @DefaultString("<prefix> No home with name <name> found.")
            Message homeNotFound();

            @ConfKey("home-already-exists")
            @DefaultString("<prefix> Home with name <name> already exists!")
            Message homeAlreadyExists();

            @ConfKey("created")
            @DefaultString("<prefix> Created new home successfully.")
            Message created();

            @ConfKey("deleted")
            @DefaultString("<prefix> Deleted home succesfully.")
            Message deleted();


        }


    }






}
