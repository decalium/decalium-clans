package org.gepron1x.clans.plugin.config.settings;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.gepron1x.clans.api.decoration.ColorDecoration;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.api.decoration.GradientDecoration;
import org.gepron1x.clans.api.decoration.SymbolDecoration;
import org.gepron1x.clans.plugin.util.message.TextMessage;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;
import space.arim.dazzleconf.sorter.AnnotationBasedSorter;

import java.util.List;

public interface Decorations {


	interface BaseDecoration {

		@AnnotationBasedSorter.Order(1)
		TextMessage name();

		@AnnotationBasedSorter.Order(2)
		Material material();

		boolean has(CombinedDecoration decoration);

		CombinedDecoration apply(CombinedDecoration decoration);
	}

	interface Gradient extends BaseDecoration {

		@AnnotationBasedSorter.Order(3)
		TextColor first();

		@AnnotationBasedSorter.Order(4)
		TextColor second();

		@Override
		default boolean has(CombinedDecoration decoration) {
			return decoration.gradient()
					.map(deco -> deco.first().equals(first()) && deco.second().equals(second()))
					.orElse(false);
		}

		@Override
		default CombinedDecoration apply(CombinedDecoration decoration) {
			return decoration.withGradient(new GradientDecoration(first(), second()));
		}
	}

	interface Color extends BaseDecoration {
		@AnnotationBasedSorter.Order(3)
		TextColor color();

		@Override
		default boolean has(CombinedDecoration decoration) {
			return decoration.color()
					.map(ColorDecoration::color).map(color()::equals)
					.orElse(false);
		}

		@Override
		default CombinedDecoration apply(CombinedDecoration decoration) {
			return decoration.withColor(color());
		}

	}

	interface Symbol extends BaseDecoration {

		@AnnotationBasedSorter.Order(3)
		String value();

		@Override
		default boolean has(CombinedDecoration decoration) {
			return decoration.symbol()
					.map(SymbolDecoration::symbol).map(value()::equals)
					.orElse(false);
		}

		@Override
		default CombinedDecoration apply(CombinedDecoration decoration) {
			return decoration.withSymbol(value());
		}


	}

	@ConfDefault.DefaultObject("emptyListDefault")
	List<@SubSection Gradient> gradients();

	@ConfDefault.DefaultObject("emptyListDefault")
	List<@SubSection Color> colors();

	@ConfDefault.DefaultObject("emptyListDefault")
	List<@SubSection Symbol> symbols();


	static <T> List<T> emptyListDefault() {
		return List.of();
	}


}
