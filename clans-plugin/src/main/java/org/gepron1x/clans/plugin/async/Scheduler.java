package org.gepron1x.clans.plugin.async;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface Scheduler {
    void runAsync(@NotNull Runnable runnable);
    void runSync(@NotNull Runnable runnable);

    @NotNull <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier);


}
