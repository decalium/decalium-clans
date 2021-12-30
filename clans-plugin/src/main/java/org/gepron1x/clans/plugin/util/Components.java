package org.gepron1x.clans.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;

public final class Components {
    private Components() {}

    public static TextComponent of(ComponentLike... likes) {
        return Component.text().append(likes).build();
    }
}
