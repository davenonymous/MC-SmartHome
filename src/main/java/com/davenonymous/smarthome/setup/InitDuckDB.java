package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.duckdb.DuckDBConnection;

import java.sql.*;

@EventBusSubscriber(modid = SmartHome.MODID)
public class InitDuckDB {
	public static DuckDBConnection connection;

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event) {
		try {
			connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:");

			Statement stmt = connection.createStatement();
			stmt.execute("CREATE TABLE items (item VARCHAR, value DECIMAL(10, 2), count INTEGER)");
			stmt.execute("INSERT INTO items VALUES ('jeans', 20.0, 1), ('hammer', 42.2, 2)");

			try (ResultSet rs = stmt.executeQuery("SELECT * FROM items")) {
				while (rs.next()) {
					SmartHome.LOGGER.info("item: {}", rs.getString(1));
					SmartHome.LOGGER.info("count: {}", rs.getInt(3));
				}
			}
			stmt.close();
		} catch (SQLException e) {
			SmartHome.LOGGER.error("Error initializing DuckDB", e);
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public static void onServerStop(ServerStoppingEvent event) {
		try {
			connection.close();
		} catch (SQLException e) {
			SmartHome.LOGGER.error("Error closing DuckDB connection", e);
		}
	}
}
