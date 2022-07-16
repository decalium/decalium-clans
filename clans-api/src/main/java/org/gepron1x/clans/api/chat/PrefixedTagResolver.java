package org.gepron1x.clans.api.chat;

import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PrefixedTagResolver implements TagResolver {
    private final String prefix;
    private final TagResolver parent;

    public static PrefixedTagResolver prefixed(TagResolver resolver, String prefix) {
        return new PrefixedTagResolver(prefix, resolver);

    }

    public PrefixedTagResolver(String prefix, TagResolver parent) {
        this.prefix = prefix;
        this.parent = parent;
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name, @NotNull ArgumentQueue arguments, @NotNull Context ctx) throws ParsingException {
        String underscored = underscored();
        if(!name.startsWith(underscored)) return null;
        return parent.resolve(name.substring(underscored.length()), arguments, ctx);
    }

    @Override
    public boolean has(@NotNull String name) {
        String underscored = underscored();
        if(!name.startsWith(underscored)) return false;
        return parent.has(name.substring(underscored.length()));
    }

    private String underscored() {
        return prefix + "_";
    }
}
