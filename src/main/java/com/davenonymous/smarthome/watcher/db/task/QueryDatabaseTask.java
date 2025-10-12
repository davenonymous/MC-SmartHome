package com.davenonymous.smarthome.watcher.db.task;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import org.duckdb.DuckDBConnection;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class QueryDatabaseTask extends DatabaseTask<ISensorData> {
	Function<DuckDBConnection, ISensorData> dbRawGetter;

	public QueryDatabaseTask(Function<DuckDBConnection, ISensorData> databaseQuery) {
		super();
		this.dbRawGetter = databaseQuery;
	}

	@Override
	public CompletableFuture<ISensorData> call() {
		future.complete(dbRawGetter.apply(connection));
		return future;
	}
}
