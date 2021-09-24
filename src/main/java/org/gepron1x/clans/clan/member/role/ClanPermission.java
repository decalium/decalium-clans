package org.gepron1x.clans.clan.member.role;

import cloud.commandframework.meta.CommandMeta;
import net.kyori.adventure.util.Index;
import org.gepron1x.clans.util.ReflectionUtils;

import java.util.Collection;

public record ClanPermission(String value) {
    public static final CommandMeta.Key<ClanPermission> CLAN_PERMISSION =
            CommandMeta.Key.of(ClanPermission.class, "clan_permission");

    public static final ClanPermission INVITE = new ClanPermission("invite_member");
    public static final ClanPermission DELETE_CLAN = new ClanPermission("delete_clan");
    public static final ClanPermission SET_ROLE = new ClanPermission("set_role");
    public static final ClanPermission SET_DISPLAY_NAME = new ClanPermission("set_display_name");
    public static final ClanPermission KICK = new ClanPermission("kick");
    public static final ClanPermission CREATE_HOME = new ClanPermission("create_home");
    public static final ClanPermission REMOVE_HOME = new ClanPermission("remove_home");

    private static final Index<String, ClanPermission> REGISTRY = Index.create(ClanPermission::value,
            ReflectionUtils.getConstants(ClanPermission.class, ClanPermission.class));

    public static Index<String, ClanPermission> registry() {
        return REGISTRY;
    }

    public static Collection<ClanPermission> all() { return REGISTRY.values(); }

}
