package com.davenonymous.smarthome.watcher;

import org.duckdb.DuckDBConnection;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class QueryDatabaseTask extends DatabaseTask<ResultSet> {
	Function<DuckDBConnection, ResultSet> dbRawGetter;

	public QueryDatabaseTask(Function<DuckDBConnection, ResultSet> databaseQuery) {
		super();
		this.dbRawGetter = databaseQuery;
	}

	@Override
	public CompletableFuture<ResultSet> call() {
		future.complete(dbRawGetter.apply(connection));
		return future;
	}
}
