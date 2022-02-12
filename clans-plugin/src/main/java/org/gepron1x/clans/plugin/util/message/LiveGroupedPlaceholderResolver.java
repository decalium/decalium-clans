package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class LiveGroupedPlaceholderResolver implements PlaceholderResolver {


    private final Iterable<? extends PlaceholderResolver> resolvers;

    LiveGroupedPlaceholderResolver(@NotNull Iterable<? extends PlaceholderResolver> resolvers) {
        this.resolvers = resolvers;
    }
    @Override
    public @Nullable Replacement<?> resolve(@NotNull String key) {
        for(PlaceholderResolver resolver : resolvers) {
            Replacement<?> replacement = resolver.resolve(key);
            if(replacement != null) return replacement;
        }
        return null;
    }
}
