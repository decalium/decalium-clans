package org.gepron1x.clans.clan.role;

import net.kyori.adventure.util.Index;

public record ClanPermission(String value) {

    public static final ClanPermission REMOVE_MEMBER = new ClanPermission("remove_member");
    public static final ClanPermission INVITE_MEMBER = new ClanPermission("invite_member");
    public static final ClanPermission DELETE_CLAN = new ClanPermission("delete_clan");
    public static final ClanPermission SET_ROLE = new ClanPermission("set_role");
    public static final ClanPermission SET_DISPLAY_NAME = new ClanPermission("set_display_name");

    public static final Index<String, ClanPermission> REGISTRY = Index.create(ClanPermission::value,
            REMOVE_MEMBER, INVITE_MEMBER, DELETE_CLAN, SET_ROLE, SET_DISPLAY_NAME);




}
