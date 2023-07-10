package org.gepron1x.clans.plugin.war.announce;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.util.player.PlayerReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.gepron1x.clans.plugin.war.TeamTitle;

import java.util.Collection;
import java.util.Objects;

public final class AnnouncingTeam implements Team {

	private final Team team;
	private final BossBar bar;
	private final transient MessagesConfig messages;

	public AnnouncingTeam(Team team, BossBar bar, MessagesConfig messages) {

		this.team = team;
		this.bar = bar;
		this.messages = messages;
	}

	@Override
	public ClanReference clan() {
		return team.clan();
	}

	@Override
	public Collection<PlayerReference> members() {
		return team.members();
	}

	@Override
	public boolean isMember(Player player) {
		return team.isMember(player);
	}

	@Override
	public Collection<PlayerReference> alive() {
		return team.alive();
	}

	@Override
	public boolean onDeath(Player player) {
		if (!team.onDeath(player)) return false;
		// members - 1
		// alive - x
		bar.progress((float) alive().size() / members().size());
		bar.name(
				new TeamTitle(this.team, this.messages)
		);
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AnnouncingTeam that = (AnnouncingTeam) o;
		return team.equals(that.team) && bar.equals(that.bar);
	}

	@Override
	public int hashCode() {
		return Objects.hash(team, bar);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("team", team)
				.add("bar", bar)
				.toString();
	}

	@Override
	public boolean isAlive() {
		return team.isAlive();
	}
}
