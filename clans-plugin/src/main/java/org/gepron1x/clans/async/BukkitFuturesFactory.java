package org.gepron1x.clans.async;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class BukkitFuturesFactory implements FuturesFactory {
    private final Logger logger;
    private final Executor asyncExecutor;
    private final Executor mainThreadExecutor;


    public BukkitFuturesFactory(@NotNull Plugin plugin) {
        this.logger = plugin.getSLF4JLogger();
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        this.mainThreadExecutor = scheduler.getMainThreadExecutor(plugin);

        this.asyncExecutor = r -> scheduler.runTaskAsynchronously(plugin, r);


    }
    @Override
    public CompletableFuture<Void> runAsync(Runnable command) {
        return setupFuture(CompletableFuture.runAsync(command, asyncExecutor));
    }

    @Override
    public CompletableFuture<Void> runSync(Runnable command) {
        return setupFuture(CompletableFuture.runAsync(command, mainThreadExecutor));
    }

    @Override
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return setupFuture(CompletableFuture.supplyAsync(supplier, asyncExecutor));
    }

    @Override
    public <T> CompletableFuture<T> supplySync(Supplier<T> supplier) {
        return setupFuture(CompletableFuture.supplyAsync(supplier, mainThreadExecutor));
    }

    @Override
    public <T> CompletableFuture<T> completedFuture(T value) {
        return CompletableFuture.completedFuture(value);
    }

    @Override
    public <T> CompletableFuture<T> failedFuture(Throwable ex) {
        return CompletableFuture.failedFuture(ex);
    }

    private <T> CompletableFuture<T> setupFuture(@NotNull CompletableFuture<T> future) {

        return future.exceptionally(t -> {
            logger.error("error happened while completing future: ", t);
            return null;
        });
    }
}
