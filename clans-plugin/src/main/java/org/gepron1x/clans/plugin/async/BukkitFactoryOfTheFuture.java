/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.async;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;
import space.arim.omnibus.util.concurrent.ReactionStage;
import space.arim.omnibus.util.concurrent.impl.BaseCentralisedFuture;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Supplier;

public final class BukkitFactoryOfTheFuture implements FactoryOfTheFuture {

    private final Executor mainThreadExecutor;
    private final ExecutorService asyncThreadExecutor;

   	public static BukkitFactoryOfTheFuture plugin(@NotNull Plugin plugin, ExecutorService asyncThreadExecutor) {
		   Server server = plugin.getServer();
		   BukkitScheduler scheduler = server.getScheduler();
		   return new BukkitFactoryOfTheFuture(r -> {
			   if(server.isPrimaryThread()) r.run();
			   scheduler.runTask(plugin, r);
		   }, asyncThreadExecutor);
	}

	public static BukkitFactoryOfTheFuture fixedThreadPool(@NotNull Plugin plugin, int threadCount) {
		   return plugin(plugin, Executors.newFixedThreadPool(threadCount));
	}

	public BukkitFactoryOfTheFuture(Executor mainThreadExecutor, ExecutorService asyncThreadExecutor) {
		this.mainThreadExecutor = mainThreadExecutor;
		this.asyncThreadExecutor = asyncThreadExecutor;
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

	public void shutdownAndTerminate() throws InterruptedException {
		   asyncThreadExecutor.shutdown();
		   asyncThreadExecutor.awaitTermination(5L, TimeUnit.SECONDS);
	}



}
