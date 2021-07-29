package org.gepron1x.clans.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.statistic.StatisticType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

public class ClanStatisticEvent extends ClanEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final StatisticType type;
    private boolean cancelled = false;
    private Integer value;

    public ClanStatisticEvent(@NotNull Clan clan, @NotNull StatisticType type, @Nullable Integer value) {
        super(clan);
        this.type = type;
        this.value = value;
    }
    @NotNull
    public StatisticType getType() {
        return type;
    }
    public OptionalInt getValue() {
        return value == null ? OptionalInt.empty() : OptionalInt.of(value);
    }
    public void setValue(@Nullable Integer value) {
        this.value = value;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
