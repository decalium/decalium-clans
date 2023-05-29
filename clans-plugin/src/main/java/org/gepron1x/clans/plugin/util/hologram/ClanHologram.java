package org.gepron1x.clans.plugin.util.hologram;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Location;

import java.util.List;

public interface ClanHologram {


	void teleport(Location location);

	void lines(List<Component> lines);

	void lines(List<Line> lines, TagResolver resolver);

	void line(int index, Component line);

	List<Component> lines();

	Location location();

}
