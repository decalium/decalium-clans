package org.gepron1x.clans.plugin.combatlog;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gepron1x.clans.api.user.Users;

public final class CombatLogListener implements Listener {

	private final Users users;

	public CombatLogListener(Users users) {

		this.users = users;
	}


	@EventHandler
	public void on(PlayerPreTagEvent event) {
		Player player = event.getPlayer();
		if(!(event.getEnemy() instanceof Player enemy)) return;
		if(users.userFor(player).clan().map(clan -> users.userFor(enemy).isIn(clan)).orElse(false)) {
			event.setCancelled(true);
		}
	}

}
