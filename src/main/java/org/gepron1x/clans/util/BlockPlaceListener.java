package org.gepron1x.clans.util;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Random;
import java.util.function.Consumer;


public class BlockPlaceListener implements Listener {
    private final NamespacedKey key;
    private final Random random = new Random();
    public BlockPlaceListener(Plugin plugin) {
        key = new NamespacedKey(plugin, "block_place_id");
    }
    private final Long2ObjectMap<Consumer<BlockPlaceEvent>> placeCallbacks = new Long2ObjectArrayMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event) {
        Long id = event.getItemInHand().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.LONG);
        if(id == null) return;
        placeCallbacks.get(id.longValue()).accept(event);
        placeCallbacks.remove(id.longValue());
    }

    public void addCallback(ItemStack item, Consumer<BlockPlaceEvent> callback) {
        long id = random.nextLong();
        item.editMeta(meta -> meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, id));
        placeCallbacks.put(id, callback);
    }
    public void removeCallback(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Long id = pdc.get(key, PersistentDataType.LONG);
        if(id == null) return;
        placeCallbacks.remove(id.longValue());
        pdc.remove(key);
        item.setItemMeta(meta);

    }
}
