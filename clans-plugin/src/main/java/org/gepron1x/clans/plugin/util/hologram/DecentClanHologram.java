package org.gepron1x.clans.plugin.util.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;

import java.util.List;

public class DecentClanHologram implements ClanHologram {

	private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR)
			.useUnusualXRepeatedCharacterHexFormat().build();

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
		}
		return new DecentClanHologram(hologram);
	}
}
