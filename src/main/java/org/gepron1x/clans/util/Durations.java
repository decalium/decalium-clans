package org.gepron1x.clans.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Durations {
    private static final byte MILLIS_TICK_LENGTH = 50;

    public static long toTicks(Duration duration) {
        return duration.toMillis() / MILLIS_TICK_LENGTH;
    }
    public static Duration of(Duration... durations) {
        long val = 0;
        for(Duration duration : durations) {
            val += duration.toNanos();
        }
        return Duration.ofNanos(val);
    }

    public static Duration ofTicks(long ticks) { return Duration.ofMillis(ticks * MILLIS_TICK_LENGTH); }

    
}
