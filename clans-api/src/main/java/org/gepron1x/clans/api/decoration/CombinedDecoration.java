package org.gepron1x.clans.api.decoration;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class CombinedDecoration implements Decoration {

	private final ColorDecoration color;
	private final GradientDecoration gradient;
	private final SymbolDecoration symbol;

	public static final CombinedDecoration EMPTY = new CombinedDecoration(null, null, null);

	public CombinedDecoration(@Nullable ColorDecoration color, @Nullable GradientDecoration gradient, @Nullable SymbolDecoration symbol) {

		this.color = color;
		this.gradient = gradient;
		this.symbol = symbol;
	}

	public CombinedDecoration withColor(ColorDecoration color) {
		return new CombinedDecoration(color, null, this.symbol);
	}

	public CombinedDecoration withColor(TextColor color) {
		return withColor(new ColorDecoration(color));
	}

	public CombinedDecoration withGradient(GradientDecoration gradient) {
		return new CombinedDecoration(null, gradient, this.symbol);
	}

	public CombinedDecoration withGradient(TextColor first, TextColor second) {
		return withGradient(new GradientDecoration(first, second));
	}

	public CombinedDecoration withSymbol(SymbolDecoration symbol) {
		return new CombinedDecoration(this.color, this.gradient, symbol);
	}

	public CombinedDecoration withSymbol(String symbol) {
		return withSymbol(new SymbolDecoration(symbol));
	}

	public Optional<ColorDecoration> color() {
		return Optional.ofNullable(color);
	}

	public Optional<GradientDecoration> gradient() {
		return Optional.ofNullable(gradient);
	}

	public Optional<SymbolDecoration> symbol() {
		return Optional.ofNullable(symbol);
	}

	@Override
	public Component apply(Component component) {
		if (symbol != null) component = symbol.apply(component);
		if (color != null) component = color.apply(component);
		if (gradient != null) component = gradient.apply(component);
		return component;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CombinedDecoration that = (CombinedDecoration) o;
		return Objects.equals(color, that.color) && Objects.equals(gradient, that.gradient) && Objects.equals(symbol, that.symbol);
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, gradient, symbol);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("color", color)
				.add("gradient", gradient)
				.add("symbol", symbol)
				.toString();
	}
}
