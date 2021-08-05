package org.gepron1x.clans.storage.task;

import org.bukkit.scheduler.BukkitRunnable;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.storage.UpdateListener;
import org.jdbi.v3.core.Jdbi;

import java.util.*;


public class DataSyncTask extends BukkitRunnable {
    private final DecaliumClans plugin;
    private final UpdateListener updateListener;
    private final Jdbi jdbi;

    public DataSyncTask(DecaliumClans plugin, UpdateListener listener) {
        this.jdbi = plugin.getJdbi();
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
