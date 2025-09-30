package com.davenonymous.smarthome.watcher;

import com.mojang.datafixers.util.Pair;
import org.duckdb.DuckDBConnection;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class VizQueryDatabaseTask extends DatabaseTask<LinkedHashMap<Pair<Instant, Long>, ?>> {
	Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> dbVizGetter;

	public VizQueryDatabaseTask(Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> databaseQuery) {
		super();
		this.dbVizGetter = databaseQuery;
	}

	public static CompletableFuture<LinkedHashMap<Pair<Instant, Long>, ?>> execute(Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> query) {
		return new VizQueryDatabaseTask(query).enqueue(WorldWatcherPool.taskQueue);
	}

	@Override
	public CompletableFuture<LinkedHashMap<Pair<Instant, Long>, ?>> call() {
		future.complete(dbVizGetter.apply(connection));
		return future;
	}
}
