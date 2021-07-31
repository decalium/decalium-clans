package org.gepron1x.clans.config;

import org.gepron1x.clans.clan.role.ClanPermission;
import org.gepron1x.clans.clan.role.ClanRole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.gepron1x.clans.storage.StorageType;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;

import java.time.Duration;
import java.util.Collections;
import java.util.List;


public interface ClansConfig {
    @ConfDefault.DefaultObject("defaultRoles")
    List<ClanRole> roles();

    @ConfDefault.DefaultString("user")
    String defaultRole();
    @ConfDefault.DefaultString("owner")
    String ownerRole();

    @SubSection SqlConfig mysql();




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
    interface SqlConfig {
        @ConfDefault.DefaultString("H2")
        StorageType storageType();
        @SubSection AuthDetails authDetails();
        interface AuthDetails {
            @ConfDefault.DefaultString("127.0.0.1")
            String host();
            @ConfDefault.DefaultString("minecraft")
            String user();
            @ConfDefault.DefaultString("pass")
            String password();
            @ConfDefault.DefaultString("clans")
            String database();
        }


        @ConfDefault.DefaultString("10m")
        @ConfComments({"sets the period between database sync.", "use this format: 3d1h30m20s"})
        Duration saveTaskPeriod();
    }
}
