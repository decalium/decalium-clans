package org.gepron1x.clans.plugin.war;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.util.Buildable;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.ClanBase;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ClanWarTeam {

    private final String clanTag;
    private final Set<UUID> members;
    private final Set<UUID> alive;

    public ClanWarTeam(String clanTag, Set<UUID> members, Set<UUID> alive) {

        this.clanTag = clanTag;
        this.members = members;
        this.alive = alive;
    }

    public void killPlayer(UUID uuid) {
        alive.remove(uuid);
    }

    public boolean isDead() {
        return alive.isEmpty();
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public String getClanTag() {
        return clanTag;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(this.members);
    }

    public Set<UUID> getAliveMembers() {
        return Collections.unmodifiableSet(this.alive);
    }

    public Audience audience(Server server) {
        Set<Player> players = new HashSet<>();
        for(UUID uuid : this.members) {
            Player player = server.getPlayer(uuid);
            if(player != null) players.add(player);
        }
        return Audience.audience(players);
    }


    public static ClanWarTeam warTeam(String clanTag, Set<UUID> members) {
        return new ClanWarTeam(clanTag, new HashSet<>(members), new HashSet<>(members));
    }


    public static final class Builder implements Buildable.Builder<ClanWarTeam> {

        private String clanTag;
        private final Set<UUID> members = new HashSet<>();

        public Builder clan(String clanTag) {
            this.clanTag = clanTag;
            return this;
        }

        public Builder clan(ClanBase clan) {
            return clan(clan.getTag());
        }

        public Builder addMember(UUID uuid) {
            this.members.add(uuid);
            return this;
        }

        public Set<UUID> members() {
            return this.members;
        }

        public Builder addMembers(Collection<UUID> members) {
            this.members.addAll(members);
            return this;
        }

        public Builder emptyMembers() {
            this.members.clear();
            return this;
        }

        @Override
        public @NotNull ClanWarTeam build() {
            return warTeam(clanTag, members);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanWarTeam that = (ClanWarTeam) o;
        return clanTag.equals(that.clanTag) && members.equals(that.members) && alive.equals(that.alive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clanTag, members, alive);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clanTag", clanTag)
                .add("members", members)
                .add("alive", alive)
                .toString();
    }
}
