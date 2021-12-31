package org.gepron1x.clans.plugin.config;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.plugin.util.Message;
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




    interface Commands {

        @SubSection
        @ConfKey("creation")
        Creation creation();

        @SubSection
        @ConfKey("deletion")
        Deletion deletion();

        @SubSection
        @ConfKey("invitation")
        Invitation invitation();

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
                    "<click:run_command:'clan accept <sender>'>Click here</click> accept invitation or type /clan accept <sender>")
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


    }






}
