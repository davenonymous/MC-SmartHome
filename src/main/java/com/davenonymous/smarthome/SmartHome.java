package com.davenonymous.smarthome;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.sql.*;

@Mod(SmartHome.MODID)
public class SmartHome {
	public static final String MODID = "smarthome";
	public static final Logger LOGGER = LogUtils.getLogger();

	public SmartHome(IEventBus modEventBus, ModContainer modContainer){
		try {
			Class.forName("org.duckdb.DuckDBDriver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		try {
			Connection conn = DriverManager.getConnection("jdbc:duckdb:");

			Statement stmt = conn.createStatement();
			stmt.execute("CREATE TABLE items (item VARCHAR, value DECIMAL(10, 2), count INTEGER)");
			stmt.execute("INSERT INTO items VALUES ('jeans', 20.0, 1), ('hammer', 42.2, 2)");

			try (ResultSet rs = stmt.executeQuery("SELECT * FROM items")) {
				while (rs.next()) {
					System.out.println(rs.getString(1));
					System.out.println(rs.getInt(3));
				}
			}
			stmt.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
