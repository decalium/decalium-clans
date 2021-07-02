package com.manya.clans.config;

import com.manya.clans.clan.role.ClanPermission;
import com.manya.clans.clan.role.ClanRole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;

import java.util.Collections;
import java.util.List;
import java.util.Map;




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
        @ConfDefault.DefaultString("127.0.0.1")
        String host();
        @ConfDefault.DefaultString("minecraft")
        String user();
        @ConfDefault.DefaultString("pass")
        String password();
        @ConfDefault.DefaultString("clans")
        String database();
    }
}
