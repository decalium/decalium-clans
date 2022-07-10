package org.gepron1x.clans.plugin.war.announce;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.announce.bossbar.BossBars;

import java.util.Collection;
import java.util.Objects;

public final class AnnouncingWar implements War {

    private final War war;
    private final BossBars bars;
    private final MessagesConfig messages;

    public AnnouncingWar(War war, BossBars bars, MessagesConfig messages) {

        this.war = war;
        this.bars = bars;
        this.messages = messages;
    }

    @Override
    public Team enemy(Team team) {
        return this.war.enemy(team);
    }

    @Override
    public Collection<Team> teams() {
        return this.war.teams();
    }

    @Override
    public boolean onPlayerDeath(Player player) {
        if(!this.war.onPlayerDeath(player)) return false;
        Audience audience = new WarAudience(this.war);
        player.getWorld().strikeLightningEffect(player.getLocation());
        if(this.war.isEnded()) {
            bars.hide(audience);
            audience.sendMessage(Component.text("War has ended."));
            this.war.teams().stream().filter(Team::isAlive).findFirst()
                    .flatMap(team -> team.clan().cached())
                    .map(Clan::displayName).ifPresent(name -> audience.sendMessage(Component.textOfChildren(name, Component.text(" wins"))));
        }
        return true;
    }

    @Override
    public boolean isEnded() {
        return this.war.isEnded();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnouncingWar that = (AnnouncingWar) o;
        return war.equals(that.war) && messages.equals(that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(war, messages);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("war", war)
                .toString();
    }
}
