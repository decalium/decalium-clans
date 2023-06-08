package org.gepron1x.clans.gui.builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.gui.DecaliumClansGui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public interface LoreApplicable {

	List<Component> SPACE_LORE = List.of(Component.space());

	List<Component> lore(TagResolver resolver);


	LoreApplicable SPACE = r -> SPACE_LORE;


	static LoreApplicable text(List<String> lines) {
		return r -> lines.stream().map(s -> {
			if(s.isBlank()) return Component.space();
			return DecaliumClansGui.MINI_MESSAGE.deserialize(s, r);
		}).toList();
	}

	static LoreApplicable spaces(int count) {
		var list = new ArrayList<Component>(count);
		for(int i = 0; i < count; i++) list.add(Component.space());
		return r -> list;
	}

	static LoreApplicable text(String... strings) {
		return text(List.of(strings));
	}

	static LoreApplicable combined(List<? extends LoreApplicable> parts) {
		return r -> {
			List<Component> components = new ArrayList<>();
			parts.forEach(applicable -> components.addAll(applicable.lore(r)));
			return components;
		};
	}

	static LoreApplicable combined(LoreApplicable... parts) {
		return combined(List.of(parts));
	}

	default LoreApplicable map(UnaryOperator<Component> mapper) {
		return r -> this.lore(r).stream().map(mapper).toList();
	}




}
