/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.gui;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.gepron1x.clans.plugin.util.message.Formatted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public final class ItemBuilder implements Formatted<ItemBuilder> {

	private TagResolver.Builder resolver;
	private String displayName;
	private List<String> lore;

	private final ItemStack item;

	ItemBuilder(ItemStack item, TagResolver.Builder resolver) {
		this.item = item;
		this.resolver = resolver;
	}

	ItemBuilder(ItemStack item) {
		this(item, TagResolver.builder());
	}

	public static ItemBuilder create(ItemStack item) {
		return new ItemBuilder(item);
	}

	public static ItemBuilder create(Material material) {
		return create(new ItemStack(material));
	}

	public static ItemBuilder skull(PlayerProfile profile) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		item.editMeta(SkullMeta.class, m -> m.setPlayerProfile(profile));
		return create(item);
	}


	@Override
	public ItemBuilder with(TagResolver tagResolver) {
		resolver.resolver(tagResolver);
		return this;
	}

	@Override
	public ItemBuilder with(String key, Tag tag) {
		resolver.tag(key, tag);
		return this;
	}

	@Override
	public ItemBuilder with(Collection<? extends TagResolver> resolvers) {
		resolver.resolvers(resolvers);
		return this;
	}

	public ItemBuilder name(String name) {
		this.displayName = name;
		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		this.lore = new ArrayList<>(lore);
		return this;
	}

	public ItemBuilder lore(String... lore) {
		this.lore = Lists.newArrayList(lore);
		return this;
	}

	public ItemBuilder amount(int amount) {
		this.item.setAmount(amount);
		return this;
	}

	public ItemBuilder edit(Consumer<ItemMeta> consumer) {
		this.item.editMeta(consumer);
		return this;
	}

	public <M extends ItemMeta> ItemBuilder edit(Class<M> metaClass, Consumer<M> consumer) {
		this.item.editMeta(metaClass, consumer);
		return this;
	}

	private Component parse(String s, TagResolver resolver) {
		return MiniMessage.miniMessage().deserialize(s, resolver).decoration(TextDecoration.ITALIC, false);
	}

	public ItemStack stack() {
		this.item.editMeta(meta -> {
			TagResolver r = resolver.build();
			Optional.ofNullable(this.displayName).map(s -> parse(s, r)).ifPresent(meta::displayName);
			Optional.ofNullable(this.lore).map(list -> list.stream().map(s -> parse(s, r)).toList()).ifPresent(meta::lore);
		});
		return this.item;
	}

	public GuiItem guiItem(Consumer<InventoryClickEvent> event) {
		return new GuiItem(stack(), event);
	}

	public GuiItem guiItem() {
		return guiItem(e -> {});
	}
}
