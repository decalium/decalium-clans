package org.gepron1x.clans.plugin.war.announce;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.reference.ClanReference;
import org.gepron1x.clans.api.war.Team;
import org.gepron1x.clans.plugin.chat.resolvers.ClanTagResolver;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.war.TeamTitle;
import org.gepron1x.clans.plugin.war.War;
import org.gepron1x.clans.plugin.war.Wars;
import org.gepron1x.clans.plugin.war.announce.bossbar.BossBars;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public final class AnnouncingWars implements Wars {
    private final Wars wars;
    private final MessagesConfig messages;

    public AnnouncingWars(Wars wars, MessagesConfig messages) {

        this.wars = wars;
        this.messages = messages;
    }

    @Override
    public void start(War war) {
        this.wars.start(war);
    }

    @Override
    public War create(Team first, Team second) {
        BossBar firstBossBar = teamBossBar(first, BossBar.Color.BLUE);
        BossBar secondBossBar = teamBossBar(second, BossBar.Color.RED);
        War war = this.wars.create(new AnnouncingTeam(first, firstBossBar, messages), new AnnouncingTeam(second, secondBossBar, messages));
        Clan firstClan = first.clan().orElseThrow();
        Clan secondClan = second.clan().orElseThrow();
        Title title = Title.title(this.messages.war().preparationTitle()
                .with(ClanTagResolver.prefixed(firstClan, "first"))
                .with(ClanTagResolver.prefixed(secondClan, "second")).asComponent(), Component.empty());

        Audience warAudience = new WarAudience(war);
        warAudience.showTitle(title);
        first.showBossBar(firstBossBar);
        first.showBossBar(secondBossBar);
        second.showBossBar(secondBossBar);
        second.showBossBar(firstBossBar);

        return new AnnouncingWar(war, new BossBars(Set.of(firstBossBar, secondBossBar)), messages);
    }

    private BossBar teamBossBar(Team team, BossBar.Color color) {
        return BossBar.bossBar(
                new TeamTitle(team, this.messages),
                1,
                color,
                BossBar.Overlay.NOTCHED_6
        );
    }

    @Override
    public Team createTeam(ClanReference ref) {
        return this.wars.createTeam(ref);
    }


    @Override
    public Optional<War> currentWar(Player player) {
        return this.wars.currentWar(player);
    }

    @Override
    public Collection<War> currentWars() {
        return this.wars.currentWars();
    }

    @Override
    public void onDeath(Player player) {
        this.wars.onDeath(player);
    }

    @Override
    public void end(War war) {
        this.wars.end(war);
    }

    @Override
    public void cleanEnded() {
        this.wars.cleanEnded();
    }
}
