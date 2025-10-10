package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.setup.dynamic.ModSensors;
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
			createMacros();
		} catch (SQLException e) {
			LOGGER.error("Error initializing DuckDB", e);
			throw new RuntimeException(e);
		}
	}

	private void createMacros() {
		String statement = "CREATE OR REPLACE MACRO avgVizData(tblName := NULL, deviceId := NULL, numericCols := NULL, otherCols := NULL, startTime := NULL, endTime := NULL, groupBySeconds := 10, maxResults := 200) AS TABLE\n" +
			"SELECT * FROM query(\n" +
			"    FORMAT(\n" +
			"        'SELECT avg(instant) as instant, max(tick) as tick, device, round(tick / {}) AS entryNum, {}{}{} FROM {} TABLESAMPLE reservoir({} ROWS) REPEATABLE(1337) WHERE instant > make_timestamp_ms({}) AND instant <= make_timestamp_ms({}) AND device = ''{}'' GROUP BY entryNum, device ORDER BY instant DESC ',\n" +
			"        20 * groupBySeconds,\n" +
			"        if(length(otherCols) > 0, array_to_string_comma_default(list_transform(otherCols, lambda c: format('first({}) AS {}', c, c))), ''),\n" +
			"        if(length(otherCols) > 0 and length(numericCols) > 0, ', ', ''),\n" +
			"        if(length(numericCols) > 0, array_to_string_comma_default(list_transform(numericCols, lambda c: format('avg({}) AS {}', c, c))), ''),        \n" +
			"        tblName,\n" +
			"        maxResults,\n" +
			"        startTime,\n" +
			"        endTime,\n" +
			"        deviceId\n" +
			"    )\n" +
			");";

		try {
			connection.prepareStatement(statement).execute();
		} catch (SQLException e) {
			LOGGER.error("Error creating DuckDB macros", e);
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
					LOGGER.info("Received poison pill, exiting world watcher loop");
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
