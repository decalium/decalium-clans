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

import org.jdbi.v3.core.HandleCallback;
import org.jdbi.v3.core.HandleConsumer;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

public interface AsyncJdbi {

	<R, X extends Exception> CentralisedFuture<R> withHandle(HandleCallback<R, X> callback);

	default <X extends Exception> CentralisedFuture<?> useHandle(HandleConsumer<X> consumer) {
		return withHandle(consumer.asCallback());
	}


	<X extends Exception> CentralisedFuture<?> useTransaction(HandleConsumer<X> consumer);


	<T> CentralisedFuture<T> failedFuture(Exception exception);

	<T> CentralisedFuture<T> completed(T t);

}
