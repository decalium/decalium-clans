package org.gepron1x.clans.plugin.war;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ClanWar implements ForwardingAudience {
    private final Set<ClanWarTeam> teams;
    private final Map<ClanWarTeam, ClanWarTeam> enemies;
    private final Server server;
    private final ClansConfig clansConfig;
    private final MessagesConfig messages;
    private final CachingClanRepository clanManager;

    public ClanWar(Set<ClanWarTeam> teams, Map<ClanWarTeam, ClanWarTeam> enemies, Server server, ClansConfig clansConfig, MessagesConfig messages, CachingClanRepository clanManager) {
        this.teams = teams;
        this.enemies = enemies;
        this.server = server;
        this.clansConfig = clansConfig;
        this.messages = messages;
        this.clanManager = clanManager;
    }

    public void start() {

    }

    public void onDeath(Player player) {
        sendMessage(messages.war().playerDied().with("member", player.displayName()));
        UUID uuid = player.getUniqueId();
        ClanWarTeam team = getTeam(uuid);
        Objects.requireNonNull(team);
        team.killPlayer(uuid);
        if(team.isDead()) {
            clanManager.requestClan(enemies.get(team).getClanTag()).thenAcceptSync(clan -> {
                if(clan == null) return;
                sendMessage(messages.war().win().with("clan", clan));
            });
        }

    }

    @Nullable
    public ClanWarTeam getTeam(@NotNull UUID uuid) {
        for(ClanWarTeam team : teams) {
            if(team.isMember(uuid)) return team;
        }
        return null;
    }


    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        Set<Audience> audiences = new HashSet<>();
        for(ClanWarTeam team : teams) {
            audiences.add(team.audience(server));
        }
        return audiences;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanWar clanWar = (ClanWar) o;
        return teams.equals(clanWar.teams) &&
                enemies.equals(clanWar.enemies) &&
                server.equals(clanWar.server) &&
                clansConfig.equals(clanWar.clansConfig) &&
                messages.equals(clanWar.messages) &&
                clanManager.equals(clanWar.clanManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teams, enemies, server, clansConfig, messages, clanManager);
    }


}
