package org.gepron1x.clans.plugin.clan.member;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public record ClanRoleImpl(String name, Component displayName, int weight, Set<ClanPermission> permissions) implements ClanRole {
    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return displayName;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public @NotNull @Unmodifiable Set<ClanPermission> getPermissions() {
        return permissions;
    }

    @Override
    public @NotNull ClanRole.Builder toBuilder() {
        return builder().name(name).displayName(displayName).weight(weight).permissions(permissions);
    }

    public static ClanRole.Builder builder() {
        return new BuilderImpl();
    }

    public static class BuilderImpl implements ClanRole.Builder {

        private String name;
        private Component displayName;
        private int weight = 0;
        private final Set<ClanPermission> permissions = new HashSet<>();

        @Override
        public @NotNull Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        @Override
        public @NotNull Builder displayName(@NotNull Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public @NotNull Builder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public @NotNull Builder permissions(@NotNull Collection<ClanPermission> permissions) {
            this.permissions.clear();
            this.permissions.addAll(permissions);
            return this;
        }

        @Override
        public @NotNull Builder addPermission(@NotNull ClanPermission permission) {
            this.permissions.add(permission);
            return this;
        }

        @Override
        public @NotNull Builder emptyPermissions() {
            this.permissions.clear();
            return this;
        }

        @Override
        public @NotNull ClanRole build() {
            return new ClanRoleImpl(name, displayName, weight, Set.copyOf(permissions));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuilderImpl builder = (BuilderImpl) o;
            return weight == builder.weight &&
                    Objects.equals(name, builder.name) &&
                    Objects.equals(displayName, builder.displayName) &&
                    permissions.equals(builder.permissions);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, displayName, weight, permissions);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("name", name)
                    .add("displayName", displayName)
                    .add("weight", weight)
                    .add("permissions", permissions)
                    .toString();
        }
    }
}
