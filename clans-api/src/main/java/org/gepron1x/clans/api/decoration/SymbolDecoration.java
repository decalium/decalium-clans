package org.gepron1x.clans.api.decoration;

import net.kyori.adventure.text.Component;

public record SymbolDecoration(String symbol) implements Decoration {


	@Override
	public Component apply(Component component) {
		Component symbol = Component.text(this.symbol);
		return Component.textOfChildren(component, Component.space(), symbol);
	}
}
