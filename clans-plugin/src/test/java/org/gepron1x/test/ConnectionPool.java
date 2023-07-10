package org.gepron1x.test;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionPool {

	@FunctionalInterface
	interface ConnectionConsumer {
		void accept(Connection connection) throws SQLException;
	}

	@FunctionalInterface
	interface ConnectionFunction<R> {
		R apply(Connection connection) throws SQLException;
	}

	private final HikariDataSource dataSource;

	public ConnectionPool(HikariDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public <R> R withConnection(ConnectionFunction<R> function) {
		try (Connection connection = this.dataSource.getConnection()) {
			return function.apply(connection);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void useConnection(ConnectionConsumer consumer) {
		withConnection(conn -> {
			consumer.accept(conn);
			return null;
		});
	}


}
