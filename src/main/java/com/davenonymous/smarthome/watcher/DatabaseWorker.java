package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;

public class DatabaseWorker extends Thread {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static DuckDBConnection connection;
	private final Path dbPath;

	private BlockingQueue<DatabaseTask<?>> taskQueue;
	private volatile boolean isRunning = true;

	public DatabaseWorker(Path dbPath, BlockingQueue<DatabaseTask<?>> taskQueue) {
		super("SmartHome DuckDB Worker");
		this.dbPath = dbPath;
		this.taskQueue = taskQueue;
		this.setDaemon(true);
	}

	private void establishConnection() {
		LOGGER.info("Establishing DuckDB connection");
		try {
			connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:" + this.dbPath);
			ModSensors.createTables(connection);
		} catch (SQLException e) {
			LOGGER.error("Error initializing DuckDB", e);
			throw new RuntimeException(e);
		}
	}

	private void closeConnection() {
		if(connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				LOGGER.error("Error closing DuckDB connection", e);
			}
		}
	}

	private void runRemainingNow() {
		if(taskQueue == null) {
			return;
		}

		List<DatabaseTask<?>> drained = new LinkedList();
		taskQueue.drainTo(drained);
		CompletableFuture<?>[] futures = new CompletableFuture[drained.size()];
		// Run all remaining tasks in the current thread
		// This is important to ensure that all tasks are completed before the server exits
		int i = 0;
		for(var remainingTask : drained) {
			remainingTask.setConnection(connection);
			futures[i++] = remainingTask.call();
		}

		CompletableFuture.allOf(futures).join();
	}

	@Override
	public void run() {
		establishConnection();

		LOGGER.info("Started Database Worker thread, entering main loop");
		try {
			while(isRunning) {
				DatabaseTask task = taskQueue.take();
				if(task == WorldWatcherPool.POISON_PILL) {
					if(SmartHome.uiRunning) {
						var stopUiTask = new ActionDatabaseTask(connection -> {
							try {
								var stmt = connection.createStatement();
								stmt.execute("CALL stop_ui_server()");
								stmt.close();
								SmartHome.uiRunning = false;
								LOGGER.info("UI server stopped");
							} catch (SQLException e) {
								LOGGER.error("Failed to stop UI server on shutdown", e);
							}
						});
						stopUiTask.enqueue(taskQueue);
					}
					LOGGER.info("Received poison pill, running remaining {} tasks before exiting thread", taskQueue.size());
					runRemainingNow();
					break;
				}
				task.setConnection(connection);
				task.run();
				task.setConnection(null);
			}
		} catch (InterruptedException e) {
			LOGGER.info("Database worker thread interrupted, running remaining {} tasks before exiting thread", taskQueue.size());
			runRemainingNow();
			Thread.currentThread().interrupt();
		}

		LOGGER.info("Exiting database worker thread, closing DuckDB connection");
		closeConnection();

		LOGGER.info("Stopped database worker");
	}

	public void close() {
		isRunning = false;
		this.interrupt();
	}
}
