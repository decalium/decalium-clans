package org.gepron1x.clans.clan.member;

import com.google.common.base.MoreObjects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.gepron1x.clans.clan.member.role.ClanPermission;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.storage.property.DefaultProperty;
import org.gepron1x.clans.storage.property.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ClanMember {
    public static final Property<ClanMember, ClanRole> ROLE = new DefaultProperty<>("role",
            ClanMember.class,
            ClanRole.class,
            member -> member.role,
            (clanMember, clanRole) -> clanMember.role = clanRole);


    private final UUID uniqueId;
    private ClanRole role;

    public ClanMember(UUID uniqueId, ClanRole role) {
        this.uniqueId = uniqueId;
        this.role = role;
    }

    public ClanMember(OfflinePlayer player, ClanRole role) {
        this(player.getUniqueId(), role);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ClanRole getRole() {
        return role;
    }
    public boolean hasPermission(ClanPermission perm) {
        return role.hasPermission(perm);
    }



    public void setRole(ClanRole role) {
        ROLE.set(this, role);
    }
    public @Nullable Player asPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }
    public @Nullable OfflinePlayer asOffline() {return Bukkit.getOfflinePlayer(uniqueId); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanMember that = (ClanMember) o;
        return uniqueId.equals(that.uniqueId) && role.equals(that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId, role);
    }
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uniqueId", uniqueId)
                .add("role", role).toString();
    }

}
