package org.gepron1x.clans.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.storage.StorageType;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.SubSection;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static space.arim.dazzleconf.annote.ConfDefault.*;


public interface ClansConfig {
    @DefaultObject("defaultRoles")
    List<ClanRole> roles();

    @DefaultString("user")
    String defaultRole();
    @DefaultString("owner")
    String ownerRole();

    @SubSection Storage storage();

    @DefaultString("Клан >> <role> <name> > <message>")
    MiniComponent clanChatFormat();




    static List<ClanRole> defaultRoles() {
        ClanRole user = new ClanRole("user",
                Component.text("Участник").color(NamedTextColor.GRAY),
                1,
                Collections.emptyList());
        ClanRole owner = new ClanRole("owner",
                Component.text("Владелец").color(NamedTextColor.DARK_RED),
                10,
                ClanPermission.REGISTRY.values());
        return List.of(user, owner);
    }
    interface Storage {
        @ConfComments({"storage service.", "H2 and MYSQL are supported."})
        @DefaultString("H2")
        StorageType storageType();

        @ConfComments("auth details. you dont need this with H2")
        @SubSection AuthDetails authDetails();

        @ConfComments({"advanced settings of pooling.", "don't touch it if you dont know what is it."})
        @SubSection HikariPool hikariPool();
        interface AuthDetails {
            @DefaultString("127.0.0.1")
            String host();
            @DefaultString("minecraft")
            String user();
            @DefaultString("pass")
            String password();
            @DefaultString("clans")
            String database();
            @DefaultBoolean(false)
            boolean useSSL();
        }

        interface HikariPool {
            @DefaultString("ClansPool")
            String poolName();
            @DefaultInteger(6)
            int maxPoolSize();
            @DefaultInteger(10)
            int maximumIdle();
            @DefaultInteger(1800000)
            int maxLifeTime();
            @DefaultInteger(5000)
            int connectionTimeOut();

        }


        @DefaultString("10m")
        @ConfComments({"sets the period between database sync.", "use this format: 3d1h30m20s"})
        Duration saveTaskPeriod();
    }
}
