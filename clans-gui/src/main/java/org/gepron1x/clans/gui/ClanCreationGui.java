package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.exception.ExceptionHandler;
import org.gepron1x.clans.api.exception.NotEnoughMoneyException;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.builder.ItemBuilder;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;

import java.util.concurrent.atomic.AtomicBoolean;

public final class ClanCreationGui implements GuiLike {
	private static final GuiItem INVALID_TAG = ItemBuilder.skullFromId("c84c3d4da61357be5410d04e85f5c8ef1eb82169b72a86e05a32f997e6ab7")
			.name("<#fb2727>Недопустимый тег!").space()
			.description("Разрешены только английские буквы.", "Мин. длина - 3 символа.").guiItem();

	private final ClanUser viewer;
	private final DecaliumClansApi clans;

	public ClanCreationGui(ClanUser viewer, DecaliumClansApi clans) {

		this.viewer = viewer;
		this.clans = clans;
	}
	@Override
	public Gui asGui() {
		ClansConfig config = JavaPlugin.getPlugin(DecaliumClansPlugin.class).config();
		AnvilGui gui = new AnvilGui("Создание клана");
		gui.setCost((short) 0);
		GuiItem confirm = ItemBuilder.skullFromId("a92e31ffb59c90ab08fc9dc1fe26802035a3a47c42fee63423bcdb4262ecb9b6")
				.name("<#92FF25>Создать клан за <price>")
				.with("price", clans.prices().clanCreation()).cancelEvent().guiItem(e -> {
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


		GuiItem paper = ItemBuilder.create(Material.PAPER).name("введите тег").cancelEvent().guiItem();

		StaticPane pane = new StaticPane(1, 1);
		pane.addItem(confirm, 0, 0);
		AtomicBoolean valid = new AtomicBoolean(true);
		StaticPane pane1 = new StaticPane(1, 1);
		gui.setOnNameInputChanged(s -> {
			if(config.displayNameFormat().tagRegex().matcher(s).matches() && !valid.get()) {
				valid.set(true);
				pane.addItem(confirm, 0, 0);
				gui.update();
			} else if(valid.get()) {
				pane.addItem(INVALID_TAG, 0, 0);
				valid.set(false);
				gui.update();
			}
		});
		pane1.addItem(paper, 0, 0);
		gui.getFirstItemComponent().addPane(pane1);
		gui.getSecondItemComponent().addPane(pane);
		gui.getResultComponent().addPane(pane1);
		return gui;
	}
}
