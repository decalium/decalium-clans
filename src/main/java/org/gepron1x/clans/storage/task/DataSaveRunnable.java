package org.gepron1x.clans.storage.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.storage.UpdateListener;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayDeque;
import java.util.Queue;


public class DataSaveRunnable extends BukkitRunnable {
    private final Plugin plugin;
    private final UpdateListener updateListener;
    private final Jdbi jdbi;

    public DataSaveRunnable(Plugin plugin, Jdbi jdbi, UpdateListener listener) {
        this.jdbi = jdbi;
        this.plugin = plugin;
        this.updateListener = listener;
    }


    @Override
    public void run() {
        plugin.getLogger().info("Save task started.");
        Queue<DatabaseUpdate> updates;
         synchronized (updateListener) {
             updates = new ArrayDeque<>(updateListener.getUpdates());
             updateListener.clearUpdates();
         }
        updates.forEach(upd -> upd.accept(jdbi));
        plugin.getLogger().info("Saved data successfully.");
    }


}
