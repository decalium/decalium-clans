package org.gepron1x.clans.plugin.war.announce.bossbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;

import java.util.Set;

public final class BossBars {

	private final Set<BossBar> bars;

	public BossBars(Set<BossBar> bars) {
		this.bars = bars;
	}

	public void hide(Audience audience) {
		bars.forEach(audience::hideBossBar);
	}
}
