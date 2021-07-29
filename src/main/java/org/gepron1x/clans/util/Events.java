package org.gepron1x.clans.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public final class Events {
    private Events() {
        throw new UnsupportedOperationException("no instances for you");
    }

    public static <E extends Event & Cancellable> boolean callCancellableEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
    public static <E extends Event> E callEvent(E event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }
}
