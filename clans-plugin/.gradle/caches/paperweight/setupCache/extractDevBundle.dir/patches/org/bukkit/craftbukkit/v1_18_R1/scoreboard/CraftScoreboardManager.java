package org.bukkit.craftbukkit.v1_18_R1.scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.util.WeakCollection;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

public final class CraftScoreboardManager implements ScoreboardManager {
    private final CraftScoreboard mainScoreboard;
    private final MinecraftServer server;
    private final Collection<CraftScoreboard> scoreboards = new WeakCollection<CraftScoreboard>();
    private final Map<CraftPlayer, CraftScoreboard> playerBoards = new HashMap<CraftPlayer, CraftScoreboard>();

    public CraftScoreboardManager(MinecraftServer minecraftserver, net.minecraft.world.scores.Scoreboard scoreboardServer) {
        this.mainScoreboard = new CraftScoreboard(scoreboardServer);
        mainScoreboard.registeredGlobally = true; // Paper
        this.server = minecraftserver;
        this.scoreboards.add(mainScoreboard);
    }

    @Override
    public CraftScoreboard getMainScoreboard() {
        return this.mainScoreboard;
    }

    @Override
    public CraftScoreboard getNewScoreboard() {
        org.spigotmc.AsyncCatcher.catchOp("scoreboard creation"); // Spigot
        CraftScoreboard scoreboard = new CraftScoreboard(new ServerScoreboard(this.server));
        // Paper start
        if (com.destroystokyo.paper.PaperConfig.trackPluginScoreboards) {
            scoreboard.registeredGlobally = true;
            scoreboards.add(scoreboard);
        }
        // Paper end
        return scoreboard;
    }

    // Paper start
    public void registerScoreboardForVanilla(CraftScoreboard scoreboard) {
        org.spigotmc.AsyncCatcher.catchOp("scoreboard registration");
        scoreboards.add(scoreboard);
    }
    // Paper end

    // CraftBukkit method
    public CraftScoreboard getPlayerBoard(CraftPlayer player) {
        CraftScoreboard board = this.playerBoards.get(player);
        return (CraftScoreboard) (board == null ? this.getMainScoreboard() : board);
    }

    // CraftBukkit method
    public void setPlayerBoard(CraftPlayer player, org.bukkit.scoreboard.Scoreboard bukkitScoreboard) throws IllegalArgumentException {
        Validate.isTrue(bukkitScoreboard instanceof CraftScoreboard, "Cannot set player scoreboard to an unregistered Scoreboard");

        CraftScoreboard scoreboard = (CraftScoreboard) bukkitScoreboard;
        net.minecraft.world.scores.Scoreboard oldboard = this.getPlayerBoard(player).getHandle();
        net.minecraft.world.scores.Scoreboard newboard = scoreboard.getHandle();
        ServerPlayer entityplayer = player.getHandle();

        if (oldboard == newboard) {
            return;
        }

        if (scoreboard == this.mainScoreboard) {
            this.playerBoards.remove(player);
        } else {
            this.playerBoards.put(player, (CraftScoreboard) scoreboard);
        }

        // Old objective tracking
        HashSet<Objective> removed = new HashSet<Objective>();
        for (int i = 0; i < 3; ++i) {
            Objective scoreboardobjective = oldboard.getDisplayObjective(i);
            if (scoreboardobjective != null && !removed.contains(scoreboardobjective)) {
                entityplayer.connection.send(new ClientboundSetObjectivePacket(scoreboardobjective, 1));
                removed.add(scoreboardobjective);
            }
        }

        // Old team tracking
        Iterator<?> iterator = oldboard.getPlayerTeams().iterator();
        while (iterator.hasNext()) {
            PlayerTeam scoreboardteam = (PlayerTeam) iterator.next();
            entityplayer.connection.send(ClientboundSetPlayerTeamPacket.createRemovePacket(scoreboardteam));
        }

        // The above is the reverse of the below method.
        this.server.getPlayerList().updateEntireScoreboard((ServerScoreboard) newboard, player.getHandle());
    }

    // CraftBukkit method
    public void removePlayer(Player player) {
        this.playerBoards.remove(player);
    }

    // CraftBukkit method
    public void getScoreboardScores(ObjectiveCriteria criteria, String name, Consumer<Score> consumer) {
        // Paper start - add timings for scoreboard search
        // plugins leaking scoreboards will make this very expensive, let server owners debug it easily
        co.aikar.timings.MinecraftTimings.scoreboardScoreSearch.startTimingIfSync();
        try {
        // Paper end - add timings for scoreboard search
        for (CraftScoreboard scoreboard : this.scoreboards) {
            Scoreboard board = scoreboard.board;
            board.forAllObjectives(criteria, name, (score) -> consumer.accept(score));
        }
        } finally { // Paper start - add timings for scoreboard search
            co.aikar.timings.MinecraftTimings.scoreboardScoreSearch.stopTimingIfSync();
        }
        // Paper end - add timings for scoreboard search
    }
}
