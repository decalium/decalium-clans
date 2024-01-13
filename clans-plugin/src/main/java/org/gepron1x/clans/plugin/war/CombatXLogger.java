package org.gepron1x.clans.plugin.war;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;

public final class CombatXLogger implements CombatLogger, Listener {

	private final ICombatManager manager;
	private final PlayerDataManager playerDataManager;

	public CombatXLogger(ICombatManager manager, PlayerDataManager playerDataManager) {

		this.manager = manager;
		this.playerDataManager = playerDataManager;
	}

	@Override
	public void tag(Player player) {
		playerDataManager.get(player).set("bossbar", false);
		manager.tag(player, null, TagType.UNKNOWN, TagReason.ATTACKED, System.currentTimeMillis() + Duration.ofDays(2).toMillis());
	}

	@Override
	public void untag(Player player) {
		if(!manager.isInCombat(player)) return;
		manager.untag(player, UntagReason.ENEMY_DEATH);
	}

	@EventHandler
	public void on(PlayerUntagEvent event) {
		playerDataManager.get(event.getPlayer()).set("bossbar", true);
	}
}
