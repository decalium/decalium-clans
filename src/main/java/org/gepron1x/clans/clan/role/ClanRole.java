package org.gepron1x.clans.clan.role;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ClanRole {
    public static final ClanRole
            OWNER = new ClanRole("owner", Component.text("Владелец"), 10, ClanPermission.REGISTRY.values()),
            USER = new ClanRole("user", Component.text("Участник"), 1, Collections.emptyList());

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
}
