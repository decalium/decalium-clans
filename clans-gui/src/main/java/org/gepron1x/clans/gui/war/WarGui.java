package org.gepron1x.clans.gui.war;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.user.ClanUser;
import org.gepron1x.clans.gui.GuiLike;

public final class WarGui implements GuiLike {

	private final ClanUser user;
	private final DecaliumClansApi clansApi;

	public WarGui(GuiLike parent, ClanUser user, DecaliumClansApi clansApi) {

		this.user = user;
		this.clansApi = clansApi;
	}
	@Override
	public Gui asGui() {
		return null;
	}
}
