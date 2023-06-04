package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class SqlQueue {

	private final ConcurrentLinkedQueue<HandleConsumer<?>> transactions;

	public SqlQueue(ConcurrentLinkedQueue<HandleConsumer<?>> transactions) {
		this.transactions = transactions;
	}

	public SqlQueue() {
		this(new ConcurrentLinkedQueue<>());
	}

	public void add(HandleConsumer<?> consumer) {
		transactions.add(consumer);
	}


	public void run(Jdbi jdbi) {
		var queue = new ArrayDeque<>(transactions);
		transactions.clear();
		try {
			jdbi.useHandle(handle -> {
				for(var consumer : queue) {
					consumer.useHandle(handle);
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
