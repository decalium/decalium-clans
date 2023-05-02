package org.gepron1x.clans.plugin.util.hologram;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import java.util.List;

public interface ClanHologram {


	void teleport(Location location);

	void lines(List<Component> lines);

	void line(int index, Component line);

	List<Component> lines();

	Location location();

}
