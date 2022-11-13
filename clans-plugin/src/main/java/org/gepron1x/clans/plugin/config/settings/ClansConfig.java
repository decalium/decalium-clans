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
package org.gepron1x.clans.plugin.config.settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.clan.member.ClanRoleImpl;
import org.gepron1x.clans.plugin.config.UserComponentSerializer;
import org.gepron1x.clans.plugin.config.format.DisplayNameFormat;
import org.gepron1x.clans.plugin.storage.StorageType;
import org.gepron1x.clans.plugin.util.message.Message;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static space.arim.dazzleconf.annote.ConfDefault.*;
@ConfHeader({"Welcome to the decalium clans config.", "Use /clan reload to reload the configuration."})
public interface ClansConfig {

    @DefaultBoolean(false)
    @ConfComments("Should we take money for actions?")
    @ConfKey("enable-economy-hook")
    boolean enableEconomy();

    @DefaultString("2m")
    @ConfKey("statistic-update-period")
    @ConfComments("How often should we update statistics? Format is 2d3h31m1s.")
    Duration statisticUpdatePeriod();


    @DefaultString("<gray>Not in clan")
    @ConfKey("not-clan-placeholder")
    @ConfComments("What should we display instead of placeholders, if player is not in the clan?")
    Component noClanPlaceholder();

    @DefaultString("MINI_MESSAGE")
    @ConfKey("user-component-format")
    @ConfComments("What format should we use for user input? MINI_MESSAGE - MiniMessage, LEGACY - legacy color codes (&a, &b, &c) or MIXED (both, minimessage and legacy)")
    UserComponentSerializer userComponentFormat();

    @ConfKey("display-names")
    @ConfComments("Display name format.")
    @SubSection DisplayNameFormat displayNameFormat();


    @ConfKey("roles")
    @SubSection
    @ConfComments({"Roles.", "Every role has name, display name, weight, and list of permissions.", "If you wish to give all permissions to the role, use *."})
    Roles roles();
    interface Roles {

        @ConfKey("default-role")
        @DefaultObject("defaultRoleDefault")
        @ConfComments("Default member role. Will be given to all joined members.")
        ClanRole defaultRole();

        @ConfKey("owner-role")
        @DefaultObject("ownerRoleDefault")
        @ConfComments("Owner member role. Given to owner, usually should have highest weight and all permissions.")
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
                                    ClanPermission.ADD_HOME, ClanPermission.REMOVE_HOME, ClanPermission.SET_ROLE, ClanPermission.ACCEPT_WAR)
                            .weight(8)
                            .build()
            );
        }


    }

    @SubSection Levels levels();

    @SubSection Homes homes();

    interface Homes {

        @ConfKey("max-homes-per-clan")
        @DefaultInteger(10)
        @ConfComments("Clan home limit.")
        int maxHomes();

        @ConfKey("max-home-display-name")
        @DefaultInteger(20)
        @ConfComments("Maximum display name size.")
        int maxHomeDisplayNameSize();

        @ConfKey("home-region-radius")
        @DefaultDouble(30.0)
        @ConfComments("Default protection radius of clan homes.")
        double homeRegionRadius();

        @ConfKey("level-region-scale")
        @DefaultDouble(0.25)
        @ConfComments("How much should we scale the region size on home upgrades?")
        double levelRegionScale();

        @ConfKey("worldguard-flags")
        @DefaultMap({"pvp", "DENY"})
        Map<String, String> worldGuardFlags();
        @ConfKey("hologram-format")
        @DefaultString("Home <home_name>")
        @ConfComments("Hologram format.")
        Message hologramFormat();


    }

    @SubSection
    @ConfKey("storage")
    @ConfComments("Storage options. At the moment only H2 and MYSQL are supported.")
    Storage storage();

    interface Storage {
        @ConfKey("type")
        @DefaultString("H2")
        @ConfComments("Type of the database. H2 or MYSQL")
        StorageType type();


        @ConfKey("auth-details")
        @ConfComments("Auth Details. Used only with MySQL storage type.")
        @SubSection AuthDetails authDetails();

        @ConfKey("hikari-pool")
        @ConfComments("Hikari Connection pool settings. Don't touch that if you don't know what are you doing.")
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
    @ConfComments("Chat settings.")
    @SubSection Chat chat();

    interface Chat {
        @ConfKey("format")
        @ConfComments({"The chat format. Available placeholders:", "<role> - Member's role.", "<member> Member's name.", "<message> - chat message."})
        @DefaultString("<role> <member> <white>➟ <#cbd4d2><message>")
        Message format();

        @ConfKey("prefix")
        @DefaultString("~")
        @ConfComments("The chat prefix. If message starts with that, it will be sent to the clan chat channel.")
        String prefix();

    }
    @ConfComments("Clan wars options.")
    @SubSection Wars wars();
    interface Wars {

        @ConfKey("disable-team-damage")
        @DefaultBoolean(true)
        @ConfComments("Disables damage between team members.")
        boolean disableTeamDamage();

        @ConfComments("Navigator options")
        @SubSection Navigation navigation();


        interface Navigation {

            @ConfKey("arrows")
            @ConfComments("Order is N NE E SE S SW W NW")
            @DefaultString("⬆⬈➡⬊⬇⬋⬅⬉")
            String arrows();

            @ConfKey("world-display-names")
            @ConfComments("How should we call worlds in the navigator?")
            @DefaultMap({"world", "World", "world_nether", "<red>Nether", "world_the_end", "<yellow>The end"})
            Map<String, Component> worldDisplayNames();
        }
    }
}
