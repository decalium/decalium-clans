package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.type.HopperGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.google.common.collect.Iterators;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.chat.ClanMemberTagResolver;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.builder.ItemBuilder;

import java.util.Iterator;
import java.util.List;

public final class RoleSelectionGui implements GuiLike {

	private final GuiLike parent;
	private final Clan clan;
	private final ClanUser viewer;
	private final ClanMember member;
	private final DecaliumClansApi clansApi;

	public RoleSelectionGui(GuiLike parent, ClanUser viewer, ClanMember member, DecaliumClansApi clansApi) {
		this.parent = parent;
		this.clan = viewer.clan().orElseThrow();
		this.viewer = viewer;
		this.member = member;
		this.clansApi = clansApi;
	}

	@Override
	public Gui asGui() {
		HopperGui gui = new HopperGui(ComponentHolder.of(Component.text("Выбор роли ").append(member)));
		gui.setOnClose(e -> parent.asGui().show(e.getPlayer()));
		ClanRole defaultRole = clansApi.roleRegistry().defaultRole();
		ClanRole helperRole = clansApi.roleRegistry().value("helper").orElseThrow();
		ClanRole ownerRole = clansApi.roleRegistry().ownerRole();
		StaticPane pane = new StaticPane(9, 1);
		pane.setOnClick(e -> e.setCancelled(true));
		Iterator<Material> materials = Iterators.forArray(Material.MUSIC_DISC_11, Material.MUSIC_DISC_WAIT);
		int i = 0;
		for (var role : List.of(defaultRole, helperRole)) {
			var builder = forRole(role, materials.next());
			pane.addItem(builder.guiItem(), i, 0);
			i += 2;
		}

		ClanMember viewerMember = viewer.member().orElseThrow();

		var owner = ItemBuilder.create(Material.MUSIC_DISC_PIGSTEP).name("<gradient:#fb2727:#fd439c>Назначить владельцем").space()
				.description("Передает клан игроку.",
						"После передачи вы <color:#fb2727>потеряете роль вдалельца</color>.").space()
				.interaction(Colors.NEGATIVE, "Нажмите для передачи!").consumer(event -> {
					ConfirmationGui.confirmAndReturn(
							DecaliumClansGui.message("Передача клана <name>").with(ClanMemberTagResolver.clanMember(member)),
							confirmEvent -> {
								clan.edit(edition -> {
									edition.owner(member).editMember(member.uniqueId(), memberEdition -> memberEdition.appoint(ownerRole))
											.editMember(viewerMember.uniqueId(), memberEdition -> memberEdition.appoint(helperRole));
								});
							}, event
					).asGui().show(event.getWhoClicked());
				}).guiItem();

		pane.addItem(owner, 4, 0);

		gui.getSlotsComponent().addPane(pane);

		return gui;
	}

	private ItemBuilder forRole(ClanRole role, Material material) {
		var builder = ItemBuilder.create(material).edit(meta -> {
			meta.displayName(role.displayName().decoration(TextDecoration.ITALIC, false));
			meta.addItemFlags(ItemFlag.values());
		});
		if (role.equals(member.role())) {
			builder.space().lore("<#42C4FB>Выбрано");
			builder.edit(meta -> meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true));
		} else {
			builder.space().interaction(Colors.POSITIVE, "Нажмите, чтобы выбрать!").consumer(e -> {
				e.getWhoClicked().closeInventory();
				new ConfirmationGui(Component.text("Вы уверены?"), confirmEvent -> {
					clan.edit(edition -> edition.editMember(member.uniqueId(), memberEdition -> memberEdition.appoint(role))).thenAcceptSync(newClan -> {
						confirmEvent.getWhoClicked().closeInventory();
						parent.asGui().show(confirmEvent.getWhoClicked());
					});
				}, () -> asGui().show(e.getWhoClicked())).asGui().show(e.getWhoClicked());
			});
		}
		return builder;
	}
}
