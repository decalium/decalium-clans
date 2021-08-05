package org.gepron1x.clans.clan.member.role;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ClanRole {

    private final String name;
    private Component displayName;
    private int weight;
    private Collection<ClanPermission> permissions;


    public ClanRole(@NotNull String name,
                    @NotNull Component displayName,
                    int weight,
                    @NotNull Collection<ClanPermission> permissions) {
        this.name = name;
        this.displayName = displayName;
        this.weight = weight;
        this.permissions = permissions;
    }
    @NotNull
    public String getName() {
        return name;
    }

    public @NotNull Component getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull Component displayName) {
        this.displayName = displayName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    @Unmodifiable
    public Collection<ClanPermission> getPermissions() {
        return Collections.unmodifiableCollection(permissions);
    }

    public boolean hasPermission(ClanPermission permission) {
        return permissions.contains(permission);
    }

    public void setPermissions(List<ClanPermission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanRole clanRole = (ClanRole) o;
        return weight == clanRole.weight &&
                name.equals(clanRole.name) &&
                displayName.equals(clanRole.displayName) &&
                permissions.equals(clanRole.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, displayName, weight, permissions);
    }
}
