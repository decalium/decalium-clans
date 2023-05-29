package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.jdbi.v3.core.HandleConsumer;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SqlQueue {


	private final AsyncJdbi jdbi;
	private final ConcurrentLinkedQueue<HandleConsumer<?>> transactions;

	public SqlQueue(AsyncJdbi jdbi, ConcurrentLinkedQueue<HandleConsumer<?>> transactions) {
		this.jdbi = jdbi;
		this.transactions = transactions;
	}

	public SqlQueue(AsyncJdbi jdbi) {
		this(jdbi, new ConcurrentLinkedQueue<>());
	}

	public void add(HandleConsumer<?> consumer) {
		transactions.add(consumer);
	}


	public CentralisedFuture<?> run() {
		var queue = new ArrayDeque<>(transactions);
		transactions.clear();
		return jdbi.useHandle(handle -> {
			for(var transaction : queue) {
				transaction.useHandle(handle);
			}
		});
	}
}
