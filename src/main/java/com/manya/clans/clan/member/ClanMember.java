package com.manya.clans.clan.member;

import com.manya.clans.clan.role.ClanPermission;
import com.manya.clans.clan.role.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClanMember {
    private final UUID uniqueId;
    private final String name;
    private ClanRole role;

    public ClanMember(UUID uniqueId, String name, ClanRole role) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.role = role;
    }
    public ClanMember(OfflinePlayer player, ClanRole role) {
        this(player.getUniqueId(), player.getName(), role);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getName() {
        return name;
    }

    public ClanRole getRole() {
        return role;
    }
    public boolean hasPermission(ClanPermission perm) {
        return role.hasPermission(perm);
    }

    public void setRole(ClanRole role) {
        this.role = role;
    }
    public @Nullable Player asPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

}
