/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import static cloud.commandframework.arguments.standard.StringArgument.StringMode;
import static cloud.commandframework.arguments.standard.StringArgument.StringParser;

public final class ComponentArgument<C> extends CommandArgument<C, Component> {
	private static final TypeToken<Component> COMPONENT_TYPE = TypeToken.get(Component.class);

	private ComponentArgument(boolean required,
							  @NonNull String name,
							  @NonNull ArgumentParser<C, Component> parser,
							  @NonNull String defaultValue,
							  @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
							  @NonNull ArgumentDescription defaultDescription) {
		super(required, name, parser, defaultValue, COMPONENT_TYPE, suggestionsProvider, defaultDescription);
	}


	public static <C> Builder<C> builder(@NonNull String name) {
		return new Builder<>(name);
	}

	public static <C> ComponentArgument<C> of(@NonNull String name, @NonNull StringMode mode) {
		return ComponentArgument.<C>builder(name).mode(mode).build();
	}

	public static <C> ComponentArgument<C> single(@NonNull String name) {
		return of(name, StringMode.SINGLE);
	}

	public static <C> ComponentArgument<C> greedy(@NonNull String name) {
		return of(name, StringMode.GREEDY);
	}

	public static <C> ComponentArgument<C> greedy(@NonNull String name, ComponentSerializer<Component, ? extends Component, String> serializer) {
		return ComponentArgument.<C>builder(name).serializer(serializer).greedy().build();
	}

	public static <C> ComponentArgument<C> quoted(@NonNull String name) {
		return of(name, StringMode.QUOTED);
	}

	public static <C> ComponentArgument<C> of(@NonNull String name) {
		return single(name);
	}

	public static <C> ComponentArgument<C> optional(@NonNull String name) {
		return optional(name, StringMode.SINGLE);
	}

	public static <C> ComponentArgument<C> optional(@NonNull String name, StringMode mode) {
		return ComponentArgument.<C>builder(name).mode(mode).asOptional().build();
	}

	public static final class Builder<C> extends CommandArgument.TypedBuilder<C, Component, Builder<C>> {
		private StringMode mode = StringMode.SINGLE;
		private ComponentSerializer<Component, ? extends Component, String> componentSerializer = MiniMessage.miniMessage();

		private Builder(@NonNull String name) {
			super(COMPONENT_TYPE, name);
			withSuggestionsProvider((v1, v2) -> Collections.emptyList());
		}

		public Builder<C> mode(@NonNull StringMode mode) {
			this.mode = mode;
			return this;
		}

		public Builder<C> single() {
			return mode(StringMode.SINGLE);
		}

		public Builder<C> greedy() {
			return mode(StringMode.GREEDY);
		}

		public Builder<C> quoted() {
			return mode(StringMode.QUOTED);
		}

		public Builder<C> serializer(@NonNull ComponentSerializer<Component, ? extends Component, String> componentSerializer) {
			this.componentSerializer = componentSerializer;
			return this;
		}

		public Builder<C> miniMessage() {
			return serializer(MiniMessage.miniMessage());
		}

		public Builder<C> gson() {
			return serializer(GsonComponentSerializer.gson());
		}

		public Builder<C> legacy() {
			return serializer(LegacyComponentSerializer.legacySection());
		}


		@Override
		public @NonNull ComponentArgument<@NonNull C> build() {
			return new ComponentArgument<>(
					isRequired(),
					getName(),
					new StringParser<>(mode, getSuggestionsProvider()).map((ctx, s) -> ArgumentParseResult.success(componentSerializer.deserialize(s))),
					getDefaultValue(),
					getSuggestionsProvider(),
					getDefaultDescription()
			);
		}
	}


}
