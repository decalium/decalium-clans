package org.gepron1x.clans.storage.property;

import com.google.common.base.Preconditions;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

public class PropertyUpdateEvent extends Event implements Cancellable {
    private final static HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Property<?, ?> property;
    private final Object target;
    private final Object value;

    public PropertyUpdateEvent(Property<?, ?> property, Object target, Object value) {

        this.property = property;
        this.target = target;
        this.value = value;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Property<?, ?> getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }
    public Object getTarget() {
        return target;
    }
    public void setValue(Object value) {
        Preconditions.checkArgument(property.getTargetType().isInstance(value),
                MessageFormat.format("value {0} is not an instance of target class {1}", value, property.getTargetType()));
    }
}
