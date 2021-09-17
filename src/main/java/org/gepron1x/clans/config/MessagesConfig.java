package org.gepron1x.clans.config;


import net.kyori.adventure.text.Component;
import org.gepron1x.clans.clan.Clan;
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
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
    @interface Placeholder {}

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

    @DefaultString("<prefix> Эта команда предназначена исключительно для игроков.")
    MiniComponent commandIsOnlyForPlayers();

    @SubSection ClanCreation creation();
    @SubSection ClanDeletion deletion();
    @SubSection ClanInvite invite();
    @SubSection ClanList clanList();
    @SubSection DisplayName displayName();
    @SubSection Member member();
    @SubSection Homes homes();


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
        @DefaultString("<prefix> <target> исключен из состава клана!")
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
        @DefaultString("<sender> invites you to the <clan> Use /clan invite accept <sender> to accept")
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
    interface Homes {
        @DefaultString("<prefix> Ошибка! Дом с названием <name> уже существует. Придумайте что-то другое!")
        MiniComponent homeWithNameAlreadyExists();
        @DefaultString("<prefix>Дом успешно создан.")
        MiniComponent success();
    }
}
