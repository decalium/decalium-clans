/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.clan.member;

import com.google.common.base.MoreObjects;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.edition.member.MemberEdition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public record ClanMemberImpl(UUID uuid,
                             ClanRole role) implements ClanMember {

    @Override
    public @NotNull UUID uniqueId() {
        return uuid;
    }

    @Override
    public @NotNull ClanRole role() {
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
        public @NotNull Builder uuid(UUID uuid) {
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

        @Override
        public void applyEdition(Consumer<MemberEdition> consumer) {
            consumer.accept(new MemberEditionImpl());
        }

        private final class MemberEditionImpl implements MemberEdition {

            @Override
            public MemberEdition appoint(@NotNull ClanRole role) {
                BuilderImpl.this.role(role);
                return this;
            }

        }
    }
}
