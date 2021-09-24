package org.gepron1x.clans.storage;


import org.bukkit.event.HandlerList;

import org.bukkit.plugin.Plugin;
import org.gepron1x.clans.DecaliumClans;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.storage.task.DataSaveRunnable;
import org.gepron1x.clans.util.Durations;
import org.jdbi.v3.core.Jdbi;

import java.time.Duration;
import java.util.List;

public class StorageService {
    private final ClanLoader loader = new ClanLoader();
    private final Plugin plugin;
    private final Jdbi jdbi;
    private final Duration updatePeriod;
    private final DataSaveRunnable dataSaveRunnable;
    private final UpdateListener updateListener;
    private final StorageType type;
    public StorageService(Plugin plugin, StorageType type, Jdbi jdbi, Duration updatePeriod) {
        this.plugin = plugin;
        this.type = type;
        this.jdbi = jdbi;
        this.updatePeriod = updatePeriod;
        this.updateListener = new UpdateListener();
        this.dataSaveRunnable = new DataSaveRunnable(plugin, jdbi, updateListener);
    }
    public void start() {
        plugin.getServer().getPluginManager()
                .registerEvents(updateListener, plugin);
        long period = Durations.toTicks(updatePeriod);
        dataSaveRunnable.runTaskTimerAsynchronously(plugin, period, period);
    }


    public List<Clan> loadClans() {
        return loader.load(jdbi);
    }


    public void shutdown() {
        dataSaveRunnable.cancel();
        dataSaveRunnable.run();
        HandlerList.unregisterAll(updateListener);
        type.onDisable(jdbi);
    }


    public Jdbi getJdbi() {
        return jdbi;
    }




}
