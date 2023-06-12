package org.gepron1x.clans.gui;

import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import org.bukkit.entity.HumanEntity;

import java.util.Objects;
import java.util.function.Consumer;

public interface GuiContainer {
	Gui gui();

	void update();

	default void show(HumanEntity player) {
		gui().show(player);
	}

	GuiContainer mapUpdate(Consumer<Gui> onUpdate);

	static Builder builder() {
		return new Builder();
	}

	static GuiContainer container(Gui gui) {
		return new Impl(gui, g -> {});
	}


	record Impl(Gui gui, Consumer<Gui> onUpdate) implements GuiContainer {


		@Override
		public void update() {
			onUpdate.accept(gui);
			gui.update();
		}

		@Override
		public GuiContainer mapUpdate(Consumer<Gui> onUpdate) {
			return new GuiContainer.Impl(gui, this.onUpdate.andThen(onUpdate));
		}
	}

	class Builder {
		private Gui gui;
		private Consumer<Gui> update = g -> {};

		public Builder gui(Gui gui) {
			this.gui = gui;
			return this;
		}

		public Builder update(Consumer<Gui> update) {
			this.update = update;
			return this;
		}

		public GuiContainer build() {
			return new GuiContainer.Impl(Objects.requireNonNull(gui), update);
		}
	}
}
