package org.gepron1x.clans.plugin.util.message;

import net.kyori.adventure.text.minimessage.placeholder.PlaceholderResolver;
import net.kyori.adventure.text.minimessage.placeholder.Replacement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

final class LiveMapPlaceholderResolver implements PlaceholderResolver {

    private final Map<String, Replacement<?>> replacementMap;

    LiveMapPlaceholderResolver(Map<String, Replacement<?>> replacementMap) {
        this.replacementMap = replacementMap;
    }


    @Override
    public @Nullable Replacement<?> resolve(@NotNull String key) {
        return replacementMap.get(key);
    }
}
