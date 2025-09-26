package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.setup.content.ModSensors;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;

public class DatabaseWorker extends Thread {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static DuckDBConnection connection;
	private final MinecraftServer server;

	private BlockingQueue<DatabaseTask> taskQueue;
	private volatile boolean isRunning = true;

	public DatabaseWorker(MinecraftServer server, BlockingQueue<DatabaseTask> taskQueue) {
		super("SmartHome DuckDB Worker");
		this.server = server;
		this.taskQueue = taskQueue;
	}

	private void establishConnection() {
		LOGGER.info("Establishing DuckDB connection");
		try {
			connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:" + server.getWorldPath(LevelResource.ROOT).resolve("smarthome.duckdb"));
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
				if(task == InitWorldWatcher.POISON_PILL) {
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
