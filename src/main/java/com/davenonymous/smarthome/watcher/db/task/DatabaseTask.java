package com.davenonymous.smarthome.watcher.db.task;

import org.duckdb.DuckDBConnection;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public abstract class DatabaseTask<T> implements Runnable {
	protected DuckDBConnection connection;
	protected CompletableFuture<T> future;

	public DatabaseTask() {
		this.future = new CompletableFuture<>();
	}

	public DatabaseTask<T> setConnection(DuckDBConnection connection) {
		this.connection = connection;
		return this;
	}

	public CompletableFuture<T> enqueue(BlockingQueue<DatabaseTask<?>> queue) {
		queue.offer(this);
		return future;
	}

	public abstract CompletableFuture<T> call();

	@Override
	public void run() {
		if(connection == null) {
			future.completeExceptionally(new IllegalStateException("No database connection"));
			return;
		}

		call();
	}
}
