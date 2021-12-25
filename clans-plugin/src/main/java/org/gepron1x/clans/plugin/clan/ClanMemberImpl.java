package org.gepron1x.clans.plugin.clan;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public record ClanMemberImpl(UUID uuid,
                             ClanRole role) implements ClanMember {

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

    @Override
    public @NotNull ClanRole getRole() {
        return role;
    }

    @Override
    public @NotNull ClanMember withRole(@NotNull ClanRole role) {
        return new ClanMemberImpl(uuid, role);
    }

    @Override
    public @NotNull ClanMember.Builder toBuilder() {
        return builder().uuid(uuid).role(role);
    }

    public static BuilderImpl builder() {
        return new BuilderImpl();
    }

    public static class BuilderImpl implements Builder {
        private UUID uuid;
        private ClanRole role;

        @Override
        public @NotNull Builder uuid(@NotNull UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public @NotNull Builder role(@NotNull ClanRole role) {
            this.role = role;
            return this;
        }

        @Override
        public @NotNull ClanMember build() {
            return new ClanMemberImpl(uuid, role);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BuilderImpl builder = (BuilderImpl) o;
            return Objects.equals(uuid, builder.uuid) && Objects.equals(role, builder.role);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid, role);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("uuid", uuid)
                    .add("role", role)
                    .toString();
        }
    }
}
