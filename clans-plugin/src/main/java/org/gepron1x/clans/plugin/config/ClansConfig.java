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

import java.time.Duration;
import java.util.List;

import static space.arim.dazzleconf.annote.ConfDefault.*;

public interface ClansConfig {

    @DefaultString("2m")
    @ConfKey("statistic-update-period")
    Duration statisticUpdatePeriod();


    @DefaultString("<gray>Not in clan")
    @ConfKey("not-clan-placeholder")
    Component noClanPlaceholder();


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
                            .displayName(Component.text("Модератор", NamedTextColor.AQUA))
                            .permissions(ClanPermission.INVITE, ClanPermission.KICK,
                                    ClanPermission.ADD_HOME, ClanPermission.SET_ROLE)
                            .weight(8)
                            .build()
            );
        }


    }

    @SubSection Homes homes();

    interface Homes {

        @ConfKey("max-homes-per-clan")
        @DefaultInteger(10)
        int maxHomes();

        @ConfKey("max-home-display-name")
        @DefaultInteger(10)
        int maxHomeDisplayNameSize();

        @ConfKey("home-region-radius")
        @DefaultDouble(30.0)
        double homeRegionRadius();

        @ConfKey("hologram-format")
        @DefaultString("База <home_name>")
        Message hologramFormat();


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

        @ConfKey("prefix")
        @DefaultString("~")
        String prefix();

    }

    @SubSection Wars wars();
    interface Wars {

        @ConfKey("disable-team-damage")
        @DefaultBoolean(true)
        boolean disableTeamDamage();
    }
}
