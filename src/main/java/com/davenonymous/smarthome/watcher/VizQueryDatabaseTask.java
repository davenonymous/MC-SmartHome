package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import org.duckdb.DuckDBConnection;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class VizQueryDatabaseTask extends DatabaseTask<IVisualizationData> {
	Function<DuckDBConnection, IVisualizationData> dbVizGetter;

	public VizQueryDatabaseTask(Function<DuckDBConnection, IVisualizationData> databaseQuery) {
		super();
		this.dbVizGetter = databaseQuery;
	}

	public static CompletableFuture<IVisualizationData> execute(Function<DuckDBConnection, IVisualizationData> query) {
		return new VizQueryDatabaseTask(query).enqueue(WorldWatcherPool.taskQueue);
	}

	@Override
	public CompletableFuture<IVisualizationData> call() {
		future.complete(dbVizGetter.apply(connection));
		return future;
	}
}
