package org.gepron1x.clans.plugin.util.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;

import java.util.List;
import java.util.Objects;

public class DecentClanHologram implements ClanHologram {

	private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.AMPERSAND_CHAR)
			.hexColors().build();

	private final Hologram hologram;

	public DecentClanHologram(Hologram hologram) {

		this.hologram = hologram;
	}
	@Override
	public void teleport(Location location) {
		DHAPI.moveHologram(hologram, location);
	}

	@Override
	public void lines(List<Component> lines) {
		DHAPI.setHologramLines(hologram, lines.stream().map(LEGACY::serialize).toList());
	}

	@Override
	public void lines(List<Line> lines, TagResolver resolver) {
		HologramPage page = hologram.getPage(0);
		for (int i = 0; i < lines.size(); i++) {
			var line = lines.get(i);
			String content = LEGACY.serialize(line.content().with(resolver).asComponent());
			if (page.size() > i) {
				HologramLine hologramLine = DHAPI.getHologramLine(page, i);
				Objects.requireNonNull(hologramLine);
				DHAPI.setHologramLine(hologramLine, content);
				hologramLine.setHeight(line.height());
			} else {
				HologramLine hologramLine = new HologramLine(page, page.getNextLineLocation(), content);
				hologramLine.setHeight(line.height());
				page.addLine(hologramLine);
			}
		}
		hologram.realignLines();
		hologram.updateAll();
	}

	@Override
	public void line(int index, Component line) {
		DHAPI.setHologramLine(hologram, index, LEGACY.serialize(line));
	}

	@Override
	public List<Component> lines() {
		return hologram.getPages().get(0).getLines().stream().<Component>map(line -> LEGACY.deserialize(line.getContent())).toList();
	}



	@Override
	public Location location() {
		return hologram.getLocation();
	}

	public static DecentClanHologram createIfAbsent(String name, Location location) {
		Hologram hologram = DHAPI.getHologram(name);
		if(hologram != null) {
			DHAPI.moveHologram(hologram, location);
		} else {
			hologram = DHAPI.createHologram(name, location);
			hologram.realignLines();
		}
		return new DecentClanHologram(hologram);
	}
}
