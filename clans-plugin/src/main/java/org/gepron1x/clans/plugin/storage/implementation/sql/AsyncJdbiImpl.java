/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
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
package org.gepron1x.clans.plugin.storage.implementation.sql;

import com.destroystokyo.paper.util.SneakyThrow;
import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import space.arim.omnibus.util.concurrent.CentralisedFuture;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.function.Supplier;

public final class AsyncJdbiImpl implements AsyncJdbi {

	private final FactoryOfTheFuture futures;
	private final Jdbi jdbi;

	public AsyncJdbiImpl(FactoryOfTheFuture futures, Jdbi jdbi) {
		this.futures = futures;
		this.jdbi = jdbi;
	}

	@Override
	public <R, X extends Exception> CentralisedFuture<R> withHandle(HandleCallback<R, X> callback) {
		return futures.supplyAsync(sneaky(() -> jdbi.withHandle(callback)));
	}

	@Override
	public <X extends Exception> CentralisedFuture<?> useTransaction(HandleConsumer<X> consumer) {
		return futures.supplyAsync(sneaky(() -> {
			jdbi.useTransaction(consumer);
			return null;
		}));
	}

	@Override
	public <T> CentralisedFuture<T> failedFuture(Exception exception) {
		return futures.failedFuture(exception);
	}

	@Override
	public <T> CentralisedFuture<T> completed(T t) {
		return futures.completedFuture(t);
	}


	private interface FailableSupplier<T, X extends Exception> extends Supplier<T> {

		@Override
		default T get() {
			try {
				return getUnchecked();
			} catch (Exception e) {
				SneakyThrow.sneaky(e);
				return null;
			}
		}

		T getUnchecked() throws X;
	}

	private static <T, X extends Exception> Supplier<T> sneaky(FailableSupplier<T, X> supplier) {
		return supplier;
	}
}
