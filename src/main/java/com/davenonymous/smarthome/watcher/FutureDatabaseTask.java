package com.davenonymous.smarthome.watcher;

import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.machinezoo.noexception.throwing.ThrowingFunction;
import org.duckdb.DuckDBConnection;

import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class FutureDatabaseTask extends DatabaseTask {
	private CompletableFuture<ResultSet> future;
	private CompletableFuture<Void> voidFuture;

	public FutureDatabaseTask(ThrowingConsumer<DuckDBConnection> action, CompletableFuture<Void> future) {
		super(action);
		this.voidFuture = future;
	}

	public FutureDatabaseTask(ThrowingFunction<DuckDBConnection, ResultSet> query, CompletableFuture<ResultSet> future) {
		super(query);
		this.future = future;
	}

	@Override
	public void run() {
		if(connection == null) {
			future.completeExceptionally(new IllegalStateException("No database connection"));
			return;
		}

		if(databaseAction != null && voidFuture != null) {
			try {
				databaseAction.accept(connection);
				voidFuture.complete(null);
			} catch (Throwable e) {
				DatabaseWorker.LOGGER.error("Error executing database task", e);
				voidFuture.completeExceptionally(e);
			}
		} else if(databaseQuery != null && future != null) {
			try {
				var resultSet = databaseQuery.apply(connection);
				future.complete(resultSet);
			} catch (Throwable e) {
				DatabaseWorker.LOGGER.error("Error executing database query", e);
				future.completeExceptionally(e);
			}
		} else {
			if(voidFuture != null) {
				voidFuture.completeExceptionally(new IllegalStateException("No database action"));
			}
			if(future != null) {
				future.completeExceptionally(new IllegalStateException("No database query"));
			}
		}
	}
}
