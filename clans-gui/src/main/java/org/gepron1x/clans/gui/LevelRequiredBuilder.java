package org.gepron1x.clans.gui;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.gui.builder.ItemBuilder;

import java.util.function.Consumer;

public final class LevelRequiredBuilder {

	private final Clan clan;
	private final int level;
	private final ItemBuilder builder;

	public LevelRequiredBuilder(Clan clan, int level, ItemBuilder builder) {

		this.clan = clan;
		this.level = level;
		this.builder = builder;
	}


	public ItemBuilder ifAllowed(Consumer<ItemBuilder> consumer) {
		if (clan.level() < level) builder.interaction(Colors.NEGATIVE, "Необходим " + level + " уровень!");
		else consumer.accept(builder);
		return builder;
	}
}
