package org.gepron1x.clans.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public record TaskScheduler(Plugin plugin) {

    public void async(Consumer<BukkitTask> task) {
        Tasks.async(plugin, task);
    }

    public void sync(Consumer<BukkitTask> task) {
        Tasks.sync(plugin, task);
    }

    public void later(Consumer<BukkitTask> task, Duration duration) {
        Tasks.later(plugin, task, duration);
    }

    public void laterAsync(Consumer<BukkitTask> task, Duration duration) {
        Tasks.laterAsync(plugin, task, duration);
    }

    public void timer(Consumer<BukkitTask> task, Duration beforeStart, Duration period) {
        Tasks.timer(plugin, task, beforeStart, period);
    }

    public void timerAsync(Consumer<BukkitTask> task, Duration beforeStart, Duration period) {
        Tasks.timerAsync(plugin, task, beforeStart, period);
    }

    public <T> Future<T> callSync(Callable<T> callable) {
        return Tasks.callSync(plugin, callable);
    }

}
