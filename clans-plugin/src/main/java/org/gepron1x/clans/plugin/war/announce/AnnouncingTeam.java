package org.gepron1x.clans.plugin.war.announce;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.util.player.PlayerReference;
import org.gepron1x.clans.plugin.war.Team;
import org.gepron1x.clans.plugin.war.TeamTitle;

import java.util.Collection;

public final class AnnouncingTeam implements Team {

    private final Team team;
    private final BossBar bar;
    private final MessagesConfig messages;

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
        if(!team.onDeath(player)) return false;
        // members - 1
        // alive - x
        bar.progress((float) alive().size() / members().size());
        bar.name(
               new TeamTitle(this.team, this.messages)
        );
        return true;
    }

    @Override
    public boolean isAlive() {
        return team.isAlive();
    }
}
