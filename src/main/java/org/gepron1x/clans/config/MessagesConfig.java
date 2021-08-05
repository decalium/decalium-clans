package org.gepron1x.clans.config;


import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.List;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface MessagesConfig {
    @DefaultString("<aqua>DecaliumClans")
    MiniComponent prefix();
    @DefaultString("<red>У вас нету права на это действие!")
    MiniComponent noPermission();
    @DefaultString("<red>Ваша роль не позволяет испольнить это!")
    MiniComponent noClanPermission();
    @DefaultString("<red>Вы не состоите в клане!")
    MiniComponent notInClan();
    @DefaultString("<prefix><red>Этот человек уже состоит в клане!")
    MiniComponent targetIsNotInClan();
    @DefaultString("Вы уже состоите в клане!")
    MiniComponent alreadyInClan();
    @SubSection ClanCreation creation();
    @SubSection ClanDeletion deletion();
    @SubSection ClanInvite invite();
    @SubSection ClanList clanList();
    @SubSection DisplayName displayName();
    @SubSection Member member();


    interface DisplayName {
        @DefaultString("<prefix> Вы успешно изменили название на <name>")
        MiniComponent success();
        @DefaultString("<prefix>Ошибка в синтаксисе!")
        MiniComponent errorInSyntax();
    }
    interface Member {
        @DefaultString("<prefix> Ошибка! у этого пользователя вес больше, чем у вас.")
        MiniComponent memberHasBiggerWeight();
        @DefaultString("<prefix>Вы не можете выдать роль с весом больше, чем у вас.")
        MiniComponent weightIsBigger();
        interface SetRole {
            @DefaultString("<prefix>Вы успешно изменили роль <target>")
            MiniComponent success();
        }
        @ConfKey("kick.success")
        MiniComponent kickSuccess();
        @SubSection SetRole setRole();

    }


    interface ClanCreation {
        @DefaultString("<green>Поздравляем! Клан <clan> успешно создан.")
        MiniComponent success();
        @DefaultString("<red>Клан с таким тегом уже существует!")
        MiniComponent clanWithTagAlreadyExists();
        @DefaultString("")
        MiniComponent notEnoughMoney();
    }

    interface ClanDeletion {
        @DefaultString("<red>Вы уверены? Напишите /clan delete confirm чтобы подтвердить действие.")
        MiniComponent confirm();
        @DefaultString("<red>Вам нечего подтверждать!")
        MiniComponent nothingToConfirm();
        @DefaultString("клан успешно удалён.")
        MiniComponent success();
    }

    interface ClanInvite {
        @DefaultString("<receiver> поулчил ваше приглашение.")
        MiniComponent invitationSent();
        @DefaultString("<sender> приглашает вас в клан <clan> Используйте /clan invite accept <sender> Для того, чтобы его принять!")
        MiniComponent invitationMessage();
        @DefaultString("Вы приняли приглашение.")
        MiniComponent accepted();
        @DefaultString("Вы отклонили приглашение.")
        MiniComponent denied();
        @DefaultString("Вы не можете пригласить самого себя! :D")
        MiniComponent cannotInviteSelf();
        @DefaultString("Вы не получали приглашений от этого игрока.")
        MiniComponent noInvitesFromThisPlayer();
        @DefaultString("Упс! Видимо, клан удалили!")
        MiniComponent clanGotDeleted();
    }

    interface ClanList {
        @DefaultString("<role> <name>")
        MiniComponent memberFormat();
        @DefaultStrings({
                "----------------------",
                "Клан <clan_name> (<clan_tag>)",
                "Создатель: <clan_creator>",
                "Участники: \n<members>"
        })
        List<MiniComponent> clanFormat();
    }
}
