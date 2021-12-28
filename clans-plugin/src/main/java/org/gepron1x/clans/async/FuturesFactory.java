package org.gepron1x.clans.async;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface FuturesFactory {

    CompletableFuture<Void> runAsync(Runnable command);
    CompletableFuture<Void> runSync(Runnable command);

    <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier);
    <T> CompletableFuture<T> supplySync(Supplier<T> supplier);

    <T> CompletableFuture<T> completedFuture(T value);
    <T> CompletableFuture<T> failedFuture(Throwable ex);


}
