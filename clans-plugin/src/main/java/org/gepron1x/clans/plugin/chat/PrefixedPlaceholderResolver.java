package org.gepron1x.clans.plugin.chat;

import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PrefixedPlaceholderResolver implements PlaceholderResolver {
    private final String prefix;
    private final String prefixUnderscored;
    private final int prefixUnderscoredLen;
    private final PlaceholderResolver parent;

    public static PrefixedPlaceholderResolver prefixed(PlaceholderResolver resolver, String prefix) {
        return new PrefixedPlaceholderResolver(prefix, resolver);

    }

    public PrefixedPlaceholderResolver(String prefix, PlaceholderResolver parent) {
        this.prefix = prefix;
        this.prefixUnderscored = prefix + "_";
        this.prefixUnderscoredLen = prefixUnderscored.length();
        this.parent = parent;
    }


    @Override
    public @Nullable Replacement<?> resolve(@NotNull String key) {
        if(!key.startsWith(prefixUnderscored)) return null;
        return parent.resolve(key.substring(prefixUnderscoredLen));
    }




    public String getPrefix() {
        return prefix;
    }

    public PlaceholderResolver getParent() {
        return parent;
    }
}
