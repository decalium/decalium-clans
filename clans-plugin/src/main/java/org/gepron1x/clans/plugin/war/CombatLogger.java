package org.gepron1x.clans.plugin.war;

import org.bukkit.entity.Player;

public interface CombatLogger {

	void tag(Player player);

	void untag(Player player);

	static CombatLogger nop() {
		return new CombatLogger() {
			@Override
			public void tag(Player player) {

			}

			@Override
			public void untag(Player player) {

			}
		};
	}
}
