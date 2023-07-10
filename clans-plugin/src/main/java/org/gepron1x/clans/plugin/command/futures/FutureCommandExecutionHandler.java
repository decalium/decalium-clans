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
package org.gepron1x.clans.plugin.command.futures;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;

public final class FutureCommandExecutionHandler<S> implements CommandExecutionHandler<S> {

	private final FutureCommandExecution<S> execution;
	private final Logger logger;

	public FutureCommandExecutionHandler(FutureCommandExecution<S> execution, Logger logger) {
		this.execution = execution;
		this.logger = logger;
	}

	@Override
	public void execute(@NonNull CommandContext<S> commandContext) {
		execution.execute(commandContext).exceptionally(t -> {
			logger.error("An error occured: ", t);
			return null;
		});

	}
}
