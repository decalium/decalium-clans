package org.gepron1x.clans.config;


import space.arim.dazzleconf.annote.SubSection;

import java.util.List;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface MessagesConfig {
    @DefaultString("<aqua>DecaliumClans")
    String prefix();
    @DefaultString("<red>У вас нету права на это действие!")
    String noPermission();
    @DefaultString("<red>Ваша роль не позволяет испольнить это!")
    String noClanPermission();
    @DefaultString("<red>Вы не состоите в клане!")
    String notInClan();
    @DefaultString("Вы уже состоите в клане!")
    String alreadyInClan();
    @SubSection ClanCreation creation();
    @SubSection ClanDeletion deletion();
    @SubSection ClanInvite invite();
    @SubSection ClanList clanList();
    @SubSection DisplayName displayName();


    interface DisplayName {
        @DefaultString("<prefix> Вы успешно изменили название на <name>")
        String success();
        @DefaultString("<prefix>Ошибка в синтаксисе!")
        String errorInSyntax();
    }


    interface ClanCreation {
        @DefaultString("<green>Поздравляем! Клан <clan> успешно создан.")
        String success();
        @DefaultString("<red>Клан с таким тегом уже существует!")
        String clanWithTagAlreadyExists();
        @DefaultString("")
        String notEnoughMoney();
    }

    interface ClanDeletion {
        @DefaultString("<red>Вы уверены? Напишите /clan delete confirm чтобы подтвердить действие.")
        String confirm();
        @DefaultString("<red>Вам нечего подтверждать!")
        String nothingToConfirm();
        @DefaultString("клан успешно удалён.")
        String success();
    }

    interface ClanInvite {
        @DefaultString("<receiver> поулчил ваше приглашение.")
        String invitationSent();
        @DefaultString("<sender> приглашает вас в клан <clan> Используйте /clan invite accept <sender> Для того, чтобы его принять!")
        String invitationMessage();
        @DefaultString("Вы приняли приглашение.")
        String accepted();
        @DefaultString("Вы отклонили приглашение.")
        String denied();
        @DefaultString("Вы не можете пригласить самого себя! :D")
        String cannotInviteSelf();
        @DefaultString("Вы не получали приглашений от этого игрока.")
        String noInvitesFromThisPlayer();
        @DefaultString("Упс! Видимо, клан удалили!")
        String clanGotDeleted();
    }

    interface ClanList {
        @DefaultString("<role> <name>")
        String memberFormat();
        @DefaultStrings({
                "----------------------",
                "Клан <clan_name> (<clan_tag>)",
                "Создатель: <clan_creator>",
                "Участники: \n<members>"
        })
        List<String> clanFormat();
    }
}
