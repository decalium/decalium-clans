package org.gepron1x.clans.api.clan.member;

import net.kyori.adventure.util.Index;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record ClanPermission(@NotNull String value) {

    public static final ClanPermission INVITE = new ClanPermission("invite");
    public static final ClanPermission KICK = new ClanPermission("kick");
    public static final ClanPermission SET_ROLE = new ClanPermission("set_role");

    public static final ClanPermission ADD_HOME = new ClanPermission("add_home");
    public static final ClanPermission REMOVE_HOME = new ClanPermission("remove_home");
    public static final ClanPermission EDIT_OTHERS_HOMES = new ClanPermission("edit_others_homes");

    public static final ClanPermission SET_DISPLAY_NAME = new ClanPermission("set_display_name");
    public static final ClanPermission PROMOTE_OWNER = new ClanPermission("promote_owner");
    public static final ClanPermission DISBAND = new ClanPermission("disband");


    public static final ClanPermission SEND_WAR_REQUEST = new ClanPermission("send_war_request");
    public static final ClanPermission ACCEPT_WAR = new ClanPermission("accept_war");

    private static final Index<String, ClanPermission> NAMES =
            Index.create(ClanPermission::value,
                    INVITE, KICK, SET_ROLE,
                    ADD_HOME, REMOVE_HOME, EDIT_OTHERS_HOMES,
                    SET_DISPLAY_NAME, PROMOTE_OWNER, DISBAND, SEND_WAR_REQUEST, ACCEPT_WAR);

    public static Index<String, ClanPermission> registry() {
        return NAMES;
    }

    public static Set<ClanPermission> all() {
        return NAMES.values();
    }

    @Override
    public String toString() {
        return value;
    }
}
