package com.davenonymous.smarthome.watcher;

import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.machinezoo.noexception.throwing.ThrowingFunction;
import org.duckdb.DuckDBConnection;

import java.sql.ResultSet;

public class DatabaseTask implements Runnable {
	DuckDBConnection connection;
	ThrowingConsumer<DuckDBConnection> databaseAction;
	ThrowingFunction<DuckDBConnection, ResultSet> databaseQuery;

	public DatabaseTask(ThrowingConsumer<DuckDBConnection> databaseAction) {
		this.databaseAction = databaseAction;
	}

	public DatabaseTask(ThrowingFunction<DuckDBConnection, ResultSet> databaseQuery) {
		this.databaseQuery = databaseQuery;
	}

	public DatabaseTask setConnection(DuckDBConnection connection) {
		this.connection = connection;
		return this;
	}

	@Override
	public void run() {
		if(connection == null) {
			return;
		}

		if(databaseAction != null) {
			try {
				databaseAction.accept(connection);
			} catch (Throwable e) {
				DatabaseWorker.LOGGER.error("Error executing database task", e);
			}
		} else if(databaseQuery != null) {
			try {
				var resultSet = databaseQuery.apply(connection);
			} catch (Throwable e) {
				DatabaseWorker.LOGGER.error("Error executing database query", e);
			}
		}

	}
}
