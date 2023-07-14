package org.gepron1x.clans.plugin.config.settings;

import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.plugin.util.DyeColors;
import org.gepron1x.clans.plugin.util.message.TextMessage;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.SubSection;

import java.util.List;

import static space.arim.dazzleconf.sorter.AnnotationBasedSorter.Order;

public interface Decorations {


	interface BaseDecoration {

		@Order(1)
		TextMessage name();


		boolean has(CombinedDecoration decoration);

		CombinedDecoration apply(CombinedDecoration decoration);

		ItemStack item();
	}



	interface GradientDecoration extends BaseDecoration {

		@Order(3)
		TextColor first();

		@Order(4)
		TextColor second();

		@Override
		default boolean has(CombinedDecoration decoration) {
			return decoration.gradient()
					.map(deco -> deco.first().equals(first()) && deco.second().equals(second()))
					.orElse(false);
		}

		@Override
		default CombinedDecoration apply(CombinedDecoration decoration) {
			return decoration.withGradient(new org.gepron1x.clans.api.decoration.GradientDecoration(first(), second()));
		}

		@Override
		default ItemStack item() {
			ItemStack item = new ItemStack(Material.WHITE_BANNER);
			DyeColor first = DyeColors.nearestTo(first());
			DyeColor second = DyeColors.nearestTo(second());
			item.editMeta(m -> {
				BannerMeta meta = (BannerMeta) m;
				meta.addPattern(new Pattern(first, PatternType.GRADIENT));
				meta.addPattern(new Pattern(second, PatternType.GRADIENT_UP));
			});

			return item;
		}
	}

	interface ColorDecoration extends BaseDecoration {
		@Order(3)
		TextColor color();

		@Override
		default boolean has(CombinedDecoration decoration) {
			return decoration.color()
					.map(org.gepron1x.clans.api.decoration.ColorDecoration::color).map(color()::equals)
					.orElse(false);
		}

		@Override
		default CombinedDecoration apply(CombinedDecoration decoration) {
			return decoration.withColor(color());
		}

		@Override
		default ItemStack item() {
			ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
			item.editMeta(m -> {
				LeatherArmorMeta meta = (LeatherArmorMeta) m;
				meta.setColor(Color.fromRGB(color().value()));
			});
			return item;
		}
	}

	interface SymbolDecoration extends BaseDecoration {
		@Order(2)
		Material material();

		@Order(3)
		String value();

		@Override
		default boolean has(CombinedDecoration decoration) {
			return decoration.symbol()
					.map(org.gepron1x.clans.api.decoration.SymbolDecoration::symbol).map(value()::equals)
					.orElse(false);
		}

		@Override
		default CombinedDecoration apply(CombinedDecoration decoration) {
			return decoration.withSymbol(value());
		}


		@Override
		default ItemStack item() {
			return new ItemStack(material());
		}
	}

	@ConfDefault.DefaultObject("emptyListDefault")
	List<@SubSection GradientDecoration> gradients();

	@ConfDefault.DefaultObject("emptyListDefault")
	List<@SubSection ColorDecoration> colors();

	@ConfDefault.DefaultObject("emptyListDefault")
	List<@SubSection SymbolDecoration> symbols();


	static <T> List<T> emptyListDefault() {
		return List.of();
	}


}
