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

    public static final ClanPermission SET_DISPLAY_NAME = new ClanPermission("set_display_name");
    public static final ClanPermission PROMOTE_OWNER = new ClanPermission("promote_owner");
    public static final ClanPermission DISBAND = new ClanPermission("disband");

    private static final Index<String, ClanPermission> NAMES =
            Index.create(ClanPermission::value,
                    INVITE, KICK, SET_ROLE,
                    ADD_HOME, REMOVE_HOME,
                    SET_DISPLAY_NAME, PROMOTE_OWNER, DISBAND);

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
