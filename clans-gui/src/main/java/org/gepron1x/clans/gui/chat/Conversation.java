package org.gepron1x.clans.gui.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class Conversation implements Listener {


	private final Plugin plugin;

	public Conversation(Plugin plugin) {

		this.plugin = plugin;
	}


	private final Map<UUID, CompletableFuture<String>> conversations = new HashMap<>();


	@EventHandler()
	@ApiStatus.Internal
	public void on(AsyncChatEvent event) {
		var future = conversations.remove(event.getPlayer().getUniqueId());
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


	public CompletableFuture<String> ask(Player player, Duration timeout) {
		var uuid = player.getUniqueId();
		var future = new CompletableFuture<String>();
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
