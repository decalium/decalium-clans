package org.gepron1x.clans.clan;

import com.google.common.base.Preconditions;
import org.gepron1x.clans.clan.role.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ClanPlayer {
    private final UUID uniqueId;
    @Nullable private ClanRole role;
    @Nullable private Clan clan;

    public ClanPlayer(OfflinePlayer player) {
        this(player.getUniqueId());
    }
    public ClanPlayer(UUID uuid) {
        this.uniqueId = uuid;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }
    @Nullable
    public Player asPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }
    @Nullable
    public OfflinePlayer asOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uniqueId);
    }

    public @Nullable ClanRole getRole() {
        return role;
    }

    public void setRole(@Nullable ClanRole role) {
        Preconditions.checkArgument(clan != null, "player is not in a clan");
        this.role = role;
    }

    public @Nullable Clan getClan() {
        return clan;
    }

    protected void setClan(@Nullable Clan clan, ClanRole role) {
        this.clan = clan;
        this.role = role;
    }
}
