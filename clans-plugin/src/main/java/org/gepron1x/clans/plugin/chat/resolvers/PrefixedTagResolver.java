package org.gepron1x.clans.plugin.chat.resolvers;

import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PrefixedTagResolver implements TagResolver.WithoutArguments {
    private final String prefix;
    private final TagResolver.WithoutArguments parent;

    public static PrefixedTagResolver prefixed(TagResolver.WithoutArguments resolver, String prefix) {
        return new PrefixedTagResolver(prefix, resolver);

    }

    public PrefixedTagResolver(String prefix, TagResolver.WithoutArguments parent) {
        this.prefix = prefix;
        this.parent = parent;
    }

    @Override
    public @Nullable Tag resolve(@NotNull String name) {
        String underscored = underscored();
        if(!name.startsWith(underscored)) return null;
        return parent.resolve(name.substring(underscored.length()));
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
