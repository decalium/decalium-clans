package org.gepron1x.clans.plugin.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class ComponentLength {

    private final Component component;

    public ComponentLength(Component component) {

        this.component = component;
    }

    public int asInt() {
        return PlainTextComponentSerializer.plainText().serialize(component).length();
    }
}
