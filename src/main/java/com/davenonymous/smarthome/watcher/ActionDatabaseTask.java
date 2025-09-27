package com.davenonymous.smarthome.watcher;

import org.duckdb.DuckDBConnection;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ActionDatabaseTask extends DatabaseTask<Void> {
	Consumer<DuckDBConnection> dbAction;

	public ActionDatabaseTask(Consumer<DuckDBConnection> databaseAction) {
		this.dbAction = databaseAction;
	}

	@Override
	public CompletableFuture<Void> call() {
		dbAction.accept(connection);
		future.complete(null);
		return future;
	}
}
