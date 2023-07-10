package org.gepron1x.clans.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.exception.DescribingException;

public final class ErrorItem {

	private static final Plugin plugin = JavaPlugin.getPlugin(DecaliumClansGui.class);

	private final InventoryClickEvent event;
	private final ComponentLike message;


	public ErrorItem(InventoryClickEvent event, ComponentLike message) {
		this.event = event;
		this.message = message;
	}

	public ErrorItem(InventoryClickEvent event, DescribingException exception) {
		this(event, exception.text(TagResolver.empty()).orElse(Component.empty()));
	}

	public ItemStack item() {
		ItemStack item = new ItemStack(Material.BARRIER);
		item.editMeta(meta -> meta.displayName(message.asComponent().decoration(TextDecoration.ITALIC, false)));
		return item;
	}

	public void show() {
		ItemStack original = event.getCurrentItem();
		event.getInventory().setItem(event.getSlot(), item());
		plugin.getServer().getScheduler().runTaskLater(plugin, () -> event.getInventory().setItem(event.getSlot(), original), 32);
	}
}
