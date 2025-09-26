package com.davenonymous.smarthome.watcher;

import com.machinezoo.noexception.throwing.ThrowingConsumer;
import org.duckdb.DuckDBConnection;

public class DatabaseTask implements Runnable {
	DuckDBConnection connection;
	ThrowingConsumer<DuckDBConnection> databaseAction;

	public DatabaseTask(ThrowingConsumer<DuckDBConnection> databaseAction) {
		this.databaseAction = databaseAction;
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

		try {
			databaseAction.accept(connection);
		} catch (Throwable ignored) {
		}
	}
}
