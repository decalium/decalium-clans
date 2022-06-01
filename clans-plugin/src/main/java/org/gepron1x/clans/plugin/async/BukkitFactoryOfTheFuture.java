package org.gepron1x.clans.plugin.async;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;
import space.arim.omnibus.util.concurrent.ReactionStage;
import space.arim.omnibus.util.concurrent.impl.BaseCentralisedFuture;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class BukkitFactoryOfTheFuture implements FactoryOfTheFuture {

    private final Executor mainThreadExecutor;
    private final Executor asyncThreadExecutor;

    public BukkitFactoryOfTheFuture(@NotNull Plugin plugin) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        this.mainThreadExecutor = r -> {
            if(plugin.getServer().isPrimaryThread()) r.run();
            scheduler.runTask(plugin, r);
        };
        this.asyncThreadExecutor = r -> scheduler.runTaskAsynchronously(plugin, r);
    }

    @Override
    public void execute(Runnable command) {
        this.asyncThreadExecutor.execute(command);

    }

    @Override
    public void executeSync(Runnable command) {
        this.mainThreadExecutor.execute(command);

    }

    @Override
    public CentralisedFuture<?> runAsync(Runnable command) {
        return supplyAsync(() -> {
            command.run();
            return null;
        });
    }

    @Override
    public CentralisedFuture<?> runAsync(Runnable command, Executor executor) {
        return supplyAsync(() -> {
            command.run();

            return null;
        }, executor);
    }

    @Override
    public CentralisedFuture<?> runSync(Runnable command) {
        return supplySync(() -> {
            command.run();
            return null;
        });
    }

    @Override
    public <T> CentralisedFuture<T> supplyAsync(Supplier<T> supplier) {
        return supplyAsync(supplier, asyncThreadExecutor);
    }

    @Override
    public <T> CentralisedFuture<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return this.<T>newIncompleteFuture().completeAsync(supplier, executor);
    }

    @Override
    public <T> CentralisedFuture<T> supplySync(Supplier<T> supplier) {
        return this.<T>newIncompleteFuture().completeSync(supplier);
    }

    @Override
    public <T> CentralisedFuture<T> completedFuture(T value) {
        CentralisedFuture<T> result = newIncompleteFuture();
        result.complete(value);
        return result;
    }

    @Override
    public <T> ReactionStage<T> completedStage(T value) {
        return completedFuture(value).minimalCompletionStage();
    }

    @Override
    public <T> CentralisedFuture<T> failedFuture(Throwable ex) {
        CentralisedFuture<T> result = newIncompleteFuture();
        result.completeExceptionally(ex);
        return result;
    }

    @Override
    public <T> ReactionStage<T> failedStage(Throwable ex) {
        return this.<T>failedFuture(ex).minimalCompletionStage();
    }

    @Override
    public <T> CentralisedFuture<T> newIncompleteFuture() {
        return new BaseCentralisedFuture<>(mainThreadExecutor::execute);
    }

    @Override
    public <T> CentralisedFuture<T> copyFuture(CompletableFuture<T> completableFuture) {
        return copyStage0(completableFuture);
    }

    @Override
    public <T> ReactionStage<T> copyStage(CompletionStage<T> completionStage) {
        return copyStage0(completionStage);
    }

    private <T> CentralisedFuture<T> copyStage0(CompletionStage<T> completionStage) {
        CentralisedFuture<T> copy = newIncompleteFuture();
        // Implicit null check
        completionStage.whenComplete((val, ex) -> {
            if (ex == null) {
                copy.complete(val);
            } else {
                copy.completeExceptionally(ex);
            }
        });
        return copy;
    }

    @Override
    public CentralisedFuture<?> allOf(CentralisedFuture<?>... futures) {
        if (futures.length == 0) { // Null check
            return completedFuture(null);
        }
        return copyFuture(CompletableFuture.allOf(futures));
    }

    @Override
    public <T> CentralisedFuture<?> allOf(Collection<? extends CentralisedFuture<T>> centralisedFutures) {
        return allOf(centralisedFutures.toArray(CentralisedFuture[]::new));
    }
}
