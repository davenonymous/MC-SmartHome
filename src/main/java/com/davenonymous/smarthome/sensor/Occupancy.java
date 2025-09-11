package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.duckdb.DuckDBConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@SmartHomeSensor(modid = "minecraft")
public class Occupancy implements ISensor {
	public static final ResourceLocation ID = SmartHome.resource("sensor/occupancy");

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public void createTables(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS occupancy (instant TIMESTAMP, tick LONG, home VARCHAR, owner UUID, zone VARCHAR, visitor VARCHAR)");
		stmt.close();
	}

	@Override
	public void visitZoneEntity(DuckDBConnection connection, MinecraftServer server, HomeZone zone, Entity entity) throws SQLException {
		if(!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		PreparedStatement prepped = connection.prepareStatement("INSERT INTO occupancy VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?)");
		prepped.setLong(1, server.getTickCount());
		prepped.setString(2, zone.home().name());
		prepped.setString(3, zone.home().owner().toString());
		prepped.setString(4, zone.name());
		prepped.setString(5, livingEntity.getName().getString());
		prepped.execute();
		prepped.close();
	}
}
