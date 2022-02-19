package org.gepron1x.clans.plugin.war;

import org.bukkit.Server;
import org.gepron1x.clans.api.CachingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class ClanWarManager {

    private final Map<UUID, ClanWar> wars = new HashMap<>();
    private final Map<String, ClanWar> clanWars = new HashMap<>();
    private final CachingClanRepository clanManager;
    private final Server server;
    private final ClansConfig config;
    private final MessagesConfig messages;

    public ClanWarManager(@NotNull CachingClanRepository clanManager, @NotNull Server server, @NotNull ClansConfig config, @NotNull MessagesConfig messages) {

        this.clanManager = clanManager;
        this.server = server;
        this.config = config;
        this.messages = messages;
    }

    public ClanWar createClanWar(@NotNull ClanWarTeam first, @NotNull ClanWarTeam second) {

        Map<ClanWarTeam, ClanWarTeam> enemies = new HashMap<>();
        enemies.put(first, second);
        enemies.put(second, first);

        return new ClanWar(Set.of(first, second), enemies, server, config, messages, clanManager);
    }

    public void endWar(@NotNull ClanWar war) {
        this.wars.entrySet().removeIf(entry -> entry.getValue().equals(war));
        this.clanWars.entrySet().removeIf(entry -> entry.getValue().equals(war));
    }
    @Nullable
    public ClanWar getClanWar(UUID uuid) {
        return wars.get(uuid);
    }





}
