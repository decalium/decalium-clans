package org.gepron1x.clans.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;

public final class Components {
    private Components() {throw new UnsupportedOperationException("no instances for u, sorry"); }
    public static Component of(ComponentLike... likes) {
        return TextComponent.ofChildren(likes);
    }

    public static Component of(Iterable<? extends ComponentLike> likes) {
        return Component.text().append(likes).build();
    }
}
