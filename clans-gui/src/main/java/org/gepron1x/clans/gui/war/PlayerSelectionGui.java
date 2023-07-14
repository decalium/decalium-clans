package org.gepron1x.clans.gui.war;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.gepron1x.clans.api.chat.ClanMemberTagResolver;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.*;
import org.gepron1x.clans.gui.builder.InteractionLoreApplicable;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.gui.builder.Lore;
import org.gepron1x.clans.gui.builder.LoreApplicable;
import org.gepron1x.clans.plugin.util.message.TextMessage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class PlayerSelectionGui implements GuiLike {


	private final ClanUser user;
	private final int count;
	private final CompletableFuture<Collection<ClanMember>> future;

	public PlayerSelectionGui(ClanUser user, int count) {

		this.user = user;
		this.count = count;
		this.future = new CompletableFuture<>();
	}
	@Override
	public Gui asGui() {
		ClanMember leader = user.member().orElseThrow();
		List<ClanMember> members = user.clan().orElseThrow().members().stream()
				.filter(member -> member.compareTo(leader) < 0 &&
						member.asPlayer(Bukkit.getServer()).isPresent())
				.collect(Collectors.toList());

		Set<ClanMember> selected = new HashSet<>();
		selected.add(leader);

		LoreApplicable selectLore = new InteractionLoreApplicable(
				LoreApplicable.text("Нажмите, чтобы взять игрока в команду"),
				Colors.POSITIVE);

		GuiItem confirm = ItemBuilder.skull(Heads.GREEN_CHECKMARK).name("<#92FF25>Подтвердить выбор").consumer(e -> {
			if(selected.size() < count) {
				new ErrorItem(e, Component.text("Недостаточно игроков!", Colors.NEGATIVE)).show();
				return;
			}
			future.complete(Set.copyOf(selected));
		}).guiItem();
		ChestGui gui = new PaginatedGui<>(6, members, member -> {
			var builder = member.asPlayer(Bukkit.getServer()).map(ItemBuilder::skull).orElseThrow();
			builder.name("<role> <name>").with(ClanMemberTagResolver.clanMember(member))
					.lore(selectLore).consumer(e -> {
						if(selected.remove(member)) {
							e.getCurrentItem().editMeta(m -> {
								m.removeEnchant(Enchantment.LUCK);
								selectLore.apply(m);
							});
						} else if(selected.size() >= count) {
							new ErrorItem(e, TextMessage.message("<#fb2727>Слишком много игроков!")).show();
							return;
						} else {
							e.getCurrentItem().editMeta(m -> {
								m.addEnchant(Enchantment.LUCK, 1, true);
								LoreApplicable.combined(
										LoreApplicable.text("<#42C4FB>Выбран"),
										Lore.interaction(Colors.NEGATIVE, "Нажмите, чтобы убрать")
								).apply(m);
								selected.add(member);
							});
						}
						confirm.getItem().editMeta(meta -> {
							LoreApplicable.combined(
									LoreApplicable.text("<#7CD8D8>Ваша команда:"),
									Lore.descriptionComponents(selected.stream().sorted()
											.map(m -> Component.text().append(m.role(), Component.space(), m).build())
											.collect(Collectors.toList()))
							).apply(meta);
						});
						Gui.getGui(e.getInventory()).update();
					});
			return builder.guiItem();
		}).asGui();
		gui.setTitle("Набор команды");
		StaticPane pane = new StaticPane(2, 6, 5, 1);
		pane.addItem(ItemBuilder.skull(Heads.DECLINE).name("<#fb2727>Отменить войну").consumer(e -> {
			e.getWhoClicked().closeInventory();
			new ConfirmationGui(Component.text("Отменить войну?"), event -> {
				event.getWhoClicked().closeInventory();
			}, () -> e.getWhoClicked().openInventory(e.getInventory())).asGui().show(e.getWhoClicked());
		}).guiItem(), 0, 0);

		return gui;
	}

	public CompletableFuture<Collection<ClanMember>> members() {
		return this.future;
	}
}
