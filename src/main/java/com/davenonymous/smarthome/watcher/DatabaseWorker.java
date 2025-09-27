package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.setup.content.ModSensors;
import com.mojang.logging.LogUtils;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

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
			} catch (SQLException e) {
				LOGGER.error("Error closing DuckDB connection", e);
			}
		}
	}

	@Override
	public void run() {
		establishConnection();

		LOGGER.info("Entering world watcher loop");
		try {
			while(isRunning) {
				DatabaseTask task = taskQueue.take();
				if(task == WorldWatcherPool.POISON_PILL) {
					break;
				}
				task.setConnection(connection);
				task.run();
				task.setConnection(null);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		LOGGER.info("Exiting world watcher loop, closing DuckDB connection");
		closeConnection();

		LOGGER.info("Stopping world watcher thread");
	}

	public void close() {
		isRunning = false;
		this.interrupt();
	}
}
