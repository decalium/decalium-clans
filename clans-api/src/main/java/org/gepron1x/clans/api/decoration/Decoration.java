package org.gepron1x.clans.api.decoration;

import net.kyori.adventure.text.Component;

import java.util.function.UnaryOperator;

public interface Decoration extends UnaryOperator<Component> {

	default Component apply(String string) {
		return apply(Component.text(string));
	}


	default Decoration with(Decoration decoration) {
		return c -> decoration.apply(apply(c));
	}


}
