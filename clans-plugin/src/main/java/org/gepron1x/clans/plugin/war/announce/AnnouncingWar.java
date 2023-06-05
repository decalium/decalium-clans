package org.gepron1x.clans.plugin.war.announce;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.chat.ClanTagResolver;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.api.war.War;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
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
        player.getWorld().strikeLightningEffect(player.getLocation());
        Audience audience = new WarAudience(this.war);
        this.messages.war().playerDied().with("member", player.displayName()).send(audience);
		if(player.getKiller() != null && team(player.getKiller()).isPresent()) {
			audience.sendTitlePart(TitlePart.SUBTITLE, this.messages.war().playerDiedSubTitle()
					.with("killer", player.getKiller().displayName())
					.with("victim", player.displayName()).asComponent());
			audience.sendTitlePart(TitlePart.TIMES, Title.DEFAULT_TIMES);
		}

        return true;
    }

    @Override
    public boolean teamWon() {
        return this.war.teamWon();
    }

    @Override
    public void finish() {
        this.war.finish();
        Audience audience = new WarAudience(this.war);
        bars.hide(audience);
        this.war.teams().stream().filter(Team::isAlive).findFirst()
                .flatMap(team -> team.clan().cached()).ifPresent(clan -> {
                    this.messages.war().win().with(ClanTagResolver.prefixed(clan)).send(audience);
                });
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
