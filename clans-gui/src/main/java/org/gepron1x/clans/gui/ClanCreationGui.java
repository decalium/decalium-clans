package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.exception.ExceptionHandler;
import org.gepron1x.clans.api.exception.NotEnoughMoneyException;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.builder.ItemBuilder;

public final class ClanCreationGui implements GuiLike {

	private static final ItemBuilder CONFIRM = ItemBuilder.skullFromId("a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6");
	private final ClanUser viewer;
	private final DecaliumClansApi clans;

	public ClanCreationGui(ClanUser viewer, DecaliumClansApi clans) {

		this.viewer = viewer;
		this.clans = clans;
	}
	@Override
	public Gui asGui() {
		AnvilGui gui = new AnvilGui("Создание клана");
		gui.setCost((short) 0);
		GuiItem confirm = CONFIRM.name("Создать клан с названием..").cancelEvent().guiItem(e -> {
			Player player = (Player) e.getWhoClicked();
			DraftClan clan = clans.draftClanBuilder().tag(gui.getRenameText())
					.displayName(Component.text(gui.getRenameText()))
					.owner(clans.memberBuilder().player(player).role(clans.roleRegistry().ownerRole()).build()).build();
			viewer.create(clan).thenAcceptSync(result -> {
				player.closeInventory();
				if(result.isSuccess()) new ClanGui(player.getServer(), result.clan(), viewer, clans).asGui().show(player);
			}).exceptionally(ExceptionHandler.catchException(NotEnoughMoneyException.class, ex -> {
				new ErrorItem(e, ex).show();
			}));
		});
		gui.setOnNameInputChanged(s -> {
			confirm.getItem().editMeta(meta -> meta.displayName(Component.text("Создать клан " + s)));
			gui.update();
		});
		StaticPane pane = new StaticPane(1, 1);
		pane.addItem(confirm, 0, 0);
		StaticPane pane1 = new StaticPane(1, 1);
		pane1.addItem(ItemBuilder.create(Material.PAPER).name("Введите название клана").cancelEvent().guiItem(), 0, 0);
		gui.getFirstItemComponent().addPane(pane1);
		gui.getResultComponent().addPane(pane);
		return gui;
	}
}
