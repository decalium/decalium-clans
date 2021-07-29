package org.gepron1x.clans.storage.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.gepron1x.clans.storage.UpdateListener;
import org.gepron1x.clans.util.Tasks;
import org.jdbi.v3.core.Jdbi;

import java.util.*;


public class DataSyncTask extends BukkitRunnable {
    private final UpdateListener updateListener;
    private final Jdbi jdbi;

    public DataSyncTask(Jdbi jdbi, UpdateListener updateListener) {
        this.jdbi = jdbi;
        this.updateListener = updateListener;
    }


    @Override
    public void run() {
        Queue<DatabaseUpdate> updates = new ArrayDeque<>(updateListener.getUpdates());
        updateListener.clearUpdates();
        Tasks.async(null, task -> {
            for(DatabaseUpdate update : updates) update.accept(jdbi);
        });

    }

}
