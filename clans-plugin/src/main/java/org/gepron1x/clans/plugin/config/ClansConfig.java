package org.gepron1x.clans.plugin.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.clan.member.ClanRoleImpl;
import org.gepron1x.clans.plugin.storage.StorageType;
import org.gepron1x.clans.plugin.util.message.Message;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.List;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface ClansConfig {

    @ConfKey("roles")
    @SubSection
    Roles roles();
    interface Roles {

        @ConfKey("default-role")
        @DefaultObject("defaultRoleDefault")
        ClanRole defaultRole();

        @ConfKey("owner-role")
        @DefaultObject("ownerRoleDefault")
        ClanRole ownerRole();

        @ConfKey("other-roles")
        @DefaultObject("otherRolesDefault")
        List<ClanRole> otherRoles();

        static ClanRole defaultRoleDefault() {
            return ClanRoleImpl.builder()
                    .name("default")
                    .displayName(Component.text("Участник", NamedTextColor.GRAY))
                    .emptyPermissions()
                    .weight(1)
                    .build();
        }

        static ClanRole ownerRoleDefault() {
            return ClanRoleImpl.builder()
                    .name("owner")
                    .displayName(Component.text("Владелец", NamedTextColor.RED))
                    .permissions(ClanPermission.all())
                    .weight(10)
                    .build();
        }

        static List<ClanRole> otherRolesDefault() {
            return List.of(
                    ClanRoleImpl.builder()
                            .name("moderator")
                            .displayName(Component.text("Модератора", NamedTextColor.AQUA))
                            .permissions(ClanPermission.INVITE, ClanPermission.KICK,
                                    ClanPermission.ADD_HOME, ClanPermission.SET_ROLE)
                            .weight(8)
                            .build()
            );
        }


    }

    @SubSection
    @ConfKey("storage")
    Storage storage();

    interface Storage {

        @ConfKey("type")
        @DefaultString("H2")
        StorageType type();


        @ConfKey("auth-details")
        @SubSection AuthDetails authDetails();

        @ConfKey("hikari-pool")
        @SubSection HikariPoolSettings hikariPool();




        interface AuthDetails {

            @ConfKey("host")
            @DefaultString("127.0.0.1:3306")
            String host();

            @ConfKey("username")
            @DefaultString("root")
            String username();

            @ConfKey("password")
            @DefaultString("pass")
            String password();

            @ConfKey("database")
            @DefaultString("clans")
            String database();

            @ConfKey("use-ssl")
            @DefaultBoolean(false)
            boolean useSSL();
        }


        interface HikariPoolSettings {

            @ConfKey("pool-name")
            @DefaultString("ClansPool")
            String poolName();

            @ConfKey("max-pool-size")
            @DefaultInteger(6)
            int maxPoolSize();

            @ConfKey("maximum-idle")
            @DefaultInteger(10)
            int maximumIdle();

            @ConfKey("max-life-time")
            @DefaultInteger(1800000)
            int maxLifeTime();

            @ConfKey("connection-timeout")
            @DefaultInteger(5000)
            int connectionTimeout();
        }


    }

    @SubSection Chat chat();

    interface Chat {
        @ConfKey("format")
        @DefaultString("<role> <member> <white>➟ <#cbd4d2><message>")
        Message format();
        
    }
}
