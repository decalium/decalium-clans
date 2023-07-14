package org.gepron1x.clans.gui.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.libraries.space.arim.omnibus.util.concurrent.CentralisedFuture;
import org.gepron1x.clans.libraries.space.arim.omnibus.util.concurrent.FactoryOfTheFuture;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Conversation implements Listener {


	private final Plugin plugin;
	private final FactoryOfTheFuture futures;

	public Conversation(Plugin plugin, FactoryOfTheFuture futures) {

		this.plugin = plugin;
		this.futures = futures;
	}


	private final Map<UUID, CentralisedFuture<String>> conversations = new HashMap<>();


	@EventHandler()
	@ApiStatus.Internal
	public void on(AsyncChatEvent event) {
		var future = conversations.remove(event.getPlayer().getUniqueId());
		if(future == null) return;
		event.setCancelled(true);
		future.complete(PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
	}

	@EventHandler(ignoreCancelled = true)
	public void on(PlayerDeathEvent event) {
		cancel(event.getEntity().getUniqueId());
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		cancel(event.getPlayer().getUniqueId());
	}


	public CentralisedFuture<String> ask(Player player, Duration timeout) {
		var uuid = player.getUniqueId();
		CentralisedFuture<String> future = futures.newIncompleteFuture();
		conversations.put(uuid, future);
		this.plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
			cancel(uuid);
		}, timeout.toMillis() / 50);
		return future;
	}

	private void cancel(UUID uuid) {
		var future = conversations.remove(uuid);
		if (future != null) future.cancel(true);
	}
}
