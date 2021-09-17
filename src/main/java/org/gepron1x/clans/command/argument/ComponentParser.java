package org.gepron1x.clans.command.argument;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;

import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.EnumMap;
import java.util.Queue;
import java.util.function.Function;

import static cloud.commandframework.arguments.standard.StringArgument.*;

public class ComponentParser<C> implements ArgumentParser<C, Component> {
    private static final EnumMap<StringMode, StringBuildingStrategy> strategies =
            new EnumMap<>(StringMode.class);
    static {
        strategies.put(StringMode.SINGLE, Queue::peek);
    }
    private StringBuildingStrategy stringBuildingStrategy;
    public ComponentParser(StringBuildingStrategy stringBuildingStrategy) {
        this.stringBuildingStrategy = stringBuildingStrategy;
    }



    @Override
    public @NonNull ArgumentParseResult<@NonNull Component> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull Queue<@NonNull String> inputQueue) {
        String input = stringBuildingStrategy.apply(inputQueue);
        return null;
    }

    @FunctionalInterface
    public interface StringBuildingStrategy extends Function<Queue<String>, String> {

    }

}
