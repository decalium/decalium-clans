package com.destroystokyo.paper.server.ticklist;

import com.destroystokyo.paper.util.set.LinkedSortedSet;
import java.util.Comparator;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.TickPriority;

// represents a set of entries to tick at a specified time
public final class TickListServerInterval<T> {

    public static final int TOTAL_PRIORITIES = TickPriority.values().length;
    public static final Comparator<TickNextTickData<?>> ENTRY_COMPARATOR_BY_ID = (entry1, entry2) -> {
        return Long.compare(entry1.getId(), entry2.getId());
    };
    public static final Comparator<TickNextTickData<?>> ENTRY_COMPARATOR = (Comparator)TickNextTickData.createTimeComparator();

    // we do not record the interval, this class is meant to be used on a ring buffer

    // inlined enum map for TickListPriority
    public final LinkedSortedSet<TickNextTickData<T>>[] byPriority = new LinkedSortedSet[TOTAL_PRIORITIES];

    {
        for (int i = 0, len = this.byPriority.length; i < len; ++i) {
            this.byPriority[i] = new LinkedSortedSet<>(ENTRY_COMPARATOR_BY_ID);
        }
    }

    public void addEntryLast(final TickNextTickData<T> entry) {
        this.byPriority[entry.priority.ordinal()].addLast(entry);
    }

    public void addEntryFirst(final TickNextTickData<T> entry) {
        this.byPriority[entry.priority.ordinal()].addFirst(entry);
    }

    public void clear() {
        for (int i = 0, len = this.byPriority.length; i < len; ++i) {
            this.byPriority[i].clear(); // O(1) clear
        }
    }
}
