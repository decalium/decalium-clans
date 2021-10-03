package org.gepron1x.clans.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.ParserException;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;

import static cloud.commandframework.arguments.standard.StringArgument.StringMode;

public class ComponentArgument<C> extends CommandArgument<C, Component> {
    private static final TypeToken<Component> COMPONENT_TYPE = TypeToken.get(Component.class);
    public ComponentArgument(boolean required,
                             @NonNull String name,
                             @NonNull ArgumentParser<C, Component> parser,
                             @NonNull String defaultValue,
                             @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
                             @NonNull ArgumentDescription defaultDescription) {
        super(required, name, parser, defaultValue, COMPONENT_TYPE, suggestionsProvider, defaultDescription);
    }
    public static <C> ComponentArgument<C> of(@NonNull String name) {
        return single(name);
    }
    public static <C> ComponentArgument<C> of(@NonNull String name, StringMode mode) {
        return ComponentArgument.<C>builder(name).mode(mode).build();
    }
    public static <C> ComponentArgument<C> optional(@NonNull String name) {
        return ComponentArgument.<C>builder(name).asOptional().build();
    }

    public static <C> Builder<C> builder(@NonNull String name) {
        return new Builder<>(name);
    }
    public static <C> ComponentArgument<C> single(@NonNull String name) {
        return of(name, StringMode.SINGLE);
    }
    public static <C> ComponentArgument<C> greedy(@NonNull String name) {
        return of(name, StringMode.GREEDY);
    }
    public static <C> ComponentArgument<C> quoted(@NonNull String name) {
        return of(name, StringMode.QUOTED);
    }

    public static class Builder<C> extends CommandArgument.TypedBuilder<C, Component, Builder<C>> {
        private StringMode mode = StringMode.SINGLE;
        private ComponentSerializer<Component, Component, String> componentSerializer = MiniMessage.get();
        private Builder(@NonNull String name) {
            super(COMPONENT_TYPE, name);
        }
        public Builder<C> mode(StringMode mode) {
            this.mode = mode;
            return this.self();
        }
        public Builder<C> greedy() {
            return mode(StringMode.GREEDY);
        }
        public Builder<C> single() {
            return mode(StringMode.SINGLE);
        }
        public Builder<C> quoted() {
            return mode(StringMode.QUOTED);
        }
        public Builder<C> serializer(ComponentSerializer<Component, Component, String> serializer) {
            this.componentSerializer = serializer;
            return this.self();
        }
        public Builder<C> miniMessage() {
            return serializer(MiniMessage.get());
        }
        public Builder<C> gson() {
            return serializer(GsonComponentSerializer.gson());
        }


        @Override
        public @NonNull ComponentArgument<C> build() {
            return new ComponentArgument<>(
                    isRequired(),
                    getName(),
                    new Parser<>(new StringArgument.StringParser<>(mode, getSuggestionsProvider()), componentSerializer),
                    getDefaultValue(),
                    getSuggestionsProvider(),
                    getDefaultDescription()
            );
        }
        public static class Parser<C> implements ArgumentParser<C, Component> {

            private final ArgumentParser<C, String> stringParser;
            private final ComponentSerializer<Component, Component, String> serializer;

            public Parser(ArgumentParser<C, String> stringParser,
                          ComponentSerializer<Component, Component, String> serializer) {
                this.stringParser = stringParser;
                this.serializer = serializer;
            }

            @Override
            public @NonNull ArgumentParseResult<@NonNull Component> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
                ArgumentParseResult<String> result = stringParser.parse(commandContext, inputQueue);
                return result.getParsedValue().map(serializer::deserializeOrNull)
                        .map(ArgumentParseResult::success)
                        .orElseGet(() ->
                                ArgumentParseResult.failure(
                                        new ComponentParserException(commandContext, stringParser, serializer)
                                )
                        );
            }
        }
        public static class ComponentParserException extends ParserException {
            private final ArgumentParser<?, String> stringParser;
            private final ComponentSerializer<? extends Component, ? extends Component, String> componentSerializer;
            protected ComponentParserException(@NonNull CommandContext<?> context, ArgumentParser<?, String> stringParser, ComponentSerializer<? extends Component, ? extends Component, String> componentSerializer) {
                super(Parser.class, context, StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING);
                this.stringParser = stringParser;
                this.componentSerializer = componentSerializer;
            }

            public ArgumentParser<?, String> getStringParser() {
                return stringParser;
            }

            public ComponentSerializer<? extends Component, ? extends Component, String> getComponentSerializer() {
                return componentSerializer;
            }
        }

    }
}
