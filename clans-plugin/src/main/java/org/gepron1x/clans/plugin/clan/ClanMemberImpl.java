package org.gepron1x.clans.plugin.clan;

import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.jetbrains.annotations.NotNull;

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

    public static class BuilderImpl implements ClanMember.Builder {
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
    }
}
