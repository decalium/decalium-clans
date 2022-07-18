package org.gepron1x.clans.plugin.command.parser;

import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.exceptions.parsing.ParserException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CaptionTagResolver implements TagResolver.WithoutArguments {

    private final ParserException exception;

    public CaptionTagResolver(ParserException exception) {
        this.exception = exception;
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name) {
        for(CaptionVariable variable : exception.captionVariables()) {
            if(variable.getKey().equals(name)) return Tag.preProcessParsed(variable.getValue());
        }
        return null;
    }
}
