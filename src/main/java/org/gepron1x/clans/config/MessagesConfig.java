package org.gepron1x.clans.config;


import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import static space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import static space.arim.dazzleconf.annote.ConfDefault.DefaultStrings;

public interface MessagesConfig {

    @DefaultString("<aqua>DecaliumClans")
    Message prefix();
    @DefaultString("<red>У вас нету права на это действие!")
    Message noPermission();
    @DefaultString("<red>Ваша роль не позволяет испольнить это!")
    Message noClanPermission();
    @DefaultString("<red>Вы не состоите в клане!")
    Message notInClan();
    @DefaultString("<prefix><red>Этот человек уже состоит в клане!")
    Message targetIsNotInClan();
    @DefaultString("Вы уже состоите в клане!")
    Message alreadyInClan();

    @DefaultString("<prefix> Эта команда предназначена исключительно для игроков.")
    Message commandIsOnlyForPlayers();

    @SubSection ClanCreation creation();
    @SubSection ClanDeletion deletion();
    @SubSection ClanInvite invite();
    @SubSection ClanList clanList();
    @SubSection DisplayName displayName();
    @SubSection Member member();
    @SubSection Homes homes();


    interface DisplayName {
        @DefaultString("<prefix> Вы успешно изменили название на <name>")
        Message success();
        @DefaultString("<prefix>Ошибка в синтаксисе!")
        Message errorInSyntax();
    }
    interface Member {
        @DefaultString("<prefix> Ошибка! у этого пользователя вес больше, чем у вас.")
        Message memberHasBiggerWeight();
        @DefaultString("<prefix>Вы не можете выдать роль с весом больше, чем у вас.")
        Message weightIsBigger();
        interface SetRole {
            @DefaultString("<prefix>Вы успешно изменили роль <target>")
            Message success();
        }
        @ConfKey("kick.success")
        @DefaultString("<prefix> <target> исключен из состава клана!")
        Message kickSuccess();
        @SubSection SetRole setRole();

    }


    interface ClanCreation {
        @DefaultString("<green>Поздравляем! Клан <clan> успешно создан.")
        Message success();
        @DefaultString("<red>Клан с таким тегом уже существует!")
        Message clanWithTagAlreadyExists();
        @DefaultString("")
        Message notEnoughMoney();
    }

    interface ClanDeletion {
        @DefaultString("<red>Вы уверены? Напишите /clan delete confirm чтобы подтвердить действие.")
        Message confirm();
        @DefaultString("<red>Вам нечего подтверждать!")
        Message nothingToConfirm();
        @DefaultString("клан успешно удалён.")
        Message success();
    }

    interface ClanInvite {
        @DefaultString("<receiver> поулчил ваше приглашение.")
        Message invitationSent();
        @DefaultString("<sender> invites you to the <clan> Use /clan invite accept <sender> to accept")
        Message invitationMessage();

        @DefaultString("Вы приняли приглашение.")
        Message accepted();
        @DefaultString("Вы отклонили приглашение.")
        Message denied();
        @DefaultString("Вы не можете пригласить самого себя! :D")
        Message cannotInviteSelf();
        @DefaultString("Вы не получали приглашений от этого игрока.")
        Message noInvitesFromThisPlayer();
        @DefaultString("Упс! Видимо, клан удалили!")
        Message clanGotDeleted();
    }

    interface ClanList {
        @DefaultString("<role> <name>")
        Message memberFormat();
        @DefaultStrings({
                "----------------------",
                "Клан <clan_name> (<clan_tag>)",
                "Создатель: <clan_creator>",
                "Участники: \n<members>"
        })
        List<Message> clanFormat();
    }
    interface Homes {
        @DefaultString("<prefix> Ошибка! Дом с названием <name> уже существует. Придумайте что-то другое!")
        Message homeWithNameAlreadyExists();
        @DefaultString("<prefix>Дом успешно создан.")
        Message success();
    }
}
