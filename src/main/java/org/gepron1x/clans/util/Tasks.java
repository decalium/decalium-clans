package org.gepron1x.clans.util;

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public final class Tasks {
    private static final byte MILLIS_TICK_LENGTH = 50;

    private Tasks() {
        throw new UnsupportedOperationException("instantiation of util class is like sex without condom; can cause something unexpected");
    }
    public static void async(Plugin plugin, Consumer<BukkitTask> task) {
       Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }
    public static void sync(Plugin plugin, Consumer<BukkitTask> task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }
    public static void later(Plugin plugin, Consumer<BukkitTask> task, Duration duration) {
        Bukkit.getScheduler().runTaskLater(plugin, task, asTicks(duration));
    }
    public static void laterAsync(Plugin plugin, Consumer<BukkitTask> task, Duration duration) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, asTicks(duration));
    }
    public static void timer(Plugin plugin, Consumer<BukkitTask> task, Duration beforeStart, Duration period) {
        Bukkit.getScheduler().runTaskTimer(plugin, task, asTicks(beforeStart), asTicks(period));
    }
    public static void timerAsync(Plugin plugin, Consumer<BukkitTask> task, Duration beforeStart, Duration period) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, asTicks(beforeStart), asTicks(period));
    }
    public static <T> Future<T> callSync(Plugin plugin, Callable<T> callable) {
        return Bukkit.getScheduler().callSyncMethod(plugin, callable);
    }

    private static long asTicks(Duration duration) {
        return duration.toMillis() / MILLIS_TICK_LENGTH;
    }
    
}
