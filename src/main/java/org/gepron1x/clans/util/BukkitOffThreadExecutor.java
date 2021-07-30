package org.gepron1x.clans.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class BukkitOffThreadExecutor implements Executor {
    private final Plugin plugin;
    private final BukkitScheduler scheduler;

    public BukkitOffThreadExecutor(Plugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getServer().getScheduler();
    }
    @Override
    public void execute(@NotNull Runnable runnable) {
        scheduler.runTaskAsynchronously(plugin, runnable);
    }
}
