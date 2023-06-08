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
package org.gepron1x.clans.gui.builder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.google.gson.JsonObject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.gepron1x.clans.gui.DecaliumClansGui;
import org.gepron1x.clans.plugin.util.message.Formatted;

import java.util.*;
import java.util.function.Consumer;

public final class ItemBuilder implements Formatted<ItemBuilder> {

	private static final Component DESCRIPTION_SEPARATOR = Component.text("├─", NamedTextColor.GRAY);
	private static final Component DESCRIPTION_SPACE = Component.text("│", NamedTextColor.GRAY);
	private static final Component DESCRIPTION_END = Component.text("└─", NamedTextColor.GRAY);

	private TagResolver.Builder resolver;
	private String displayName;
	private List<LoreApplicable> lore = new ArrayList<>();
	private static Component parse(String s, TagResolver resolver) {
		return DecaliumClansGui.MINI_MESSAGE.deserialize(s, resolver).colorIfAbsent(NamedTextColor.WHITE);
	}

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
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setPlayerProfile(profile);
		item.setItemMeta(meta);
		return create(item);
	}

	public static ItemBuilder skull(String base64) {
		PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
		profile.setProperty(new ProfileProperty("textures", base64));
		return skull(profile);
	}

	public static ItemBuilder skullFromId(String id) {
		String url = "http://textures.minecraft.net/texture/"+id;
		JsonObject skin = new JsonObject();
		skin.addProperty("url", url);
		JsonObject textures = new JsonObject();
		textures.add("SKIN", skin);
		JsonObject object = new JsonObject();
		object.add("textures", textures);
		return skull(Base64.getEncoder().encodeToString(object.toString().getBytes()));
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
		return lore(LoreApplicable.text(lore));
	}

	public ItemBuilder lore(LoreApplicable applicable) {
		this.lore.add(applicable);
		return this;
	}

	public ItemBuilder description(String... strings) {
		return lore(new DescriptionLoreApplicable(LoreApplicable.text(strings)));
	}

	public ItemBuilder interaction(TextColor color, List<String> strings) {
		return lore(new InteractionLoreApplicable(LoreApplicable.text(strings), color));
	}

	public ItemBuilder menuInteraction(TextColor color) {
		return interaction(color, "Нажмите, чтобы перейти в меню");
	}
	public ItemBuilder menuInteraction() {
		return menuInteraction(InteractionLoreApplicable.POSITIVE);
	}

	public ItemBuilder interaction(TextColor color, String... strings) {
		return interaction(color, List.of(strings));
	}

	public ItemBuilder space() {
		return lore(LoreApplicable.SPACE);
	}

	public ItemBuilder lore(String... lore) {
		return lore(LoreApplicable.text(lore));
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

	private static Component cleanItalic(Component component) {
		if(component.hasDecoration(TextDecoration.ITALIC)) return component;
		return component.decoration(TextDecoration.ITALIC, false);
	}

	public ItemStack stack() {
		this.item.editMeta(meta -> {
			TagResolver r = resolver.build();
			Optional.ofNullable(this.displayName).map(s -> parse(s, r)).ifPresent(meta::displayName);
			Optional.ofNullable(this.lore).map(list -> LoreApplicable.combined(list).map(ItemBuilder::cleanItalic).lore(r)).ifPresent(meta::lore);
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
