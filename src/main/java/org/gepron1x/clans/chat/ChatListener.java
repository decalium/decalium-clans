package org.gepron1x.clans.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.gepron1x.clans.config.ClansConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatListener implements Listener {
    private final Set<UUID> clanChatUsers = new HashSet<>();

    public ChatListener(ClansConfig config) {

    }

    public void toggleClanChat(Player player) {
        UUID uuid = player.getUniqueId();
        if(!clanChatUsers.remove(uuid)) clanChatUsers.add(uuid);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

    }
  


}
