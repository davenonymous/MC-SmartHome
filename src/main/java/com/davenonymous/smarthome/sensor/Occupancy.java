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
		stmt.execute("CREATE TABLE IF NOT EXISTS occupancy (instant TIMESTAMP, tick LONG, home UUID, zone VARCHAR, visitor VARCHAR, pos STRUCT(x DOUBLE, y DOUBLE, z DOUBLE))");
		stmt.close();
	}

	@Override
	public void visitZoneEntity(DuckDBConnection connection, MinecraftServer server, HomeZone zone, Entity entity) throws SQLException {
		if(!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		PreparedStatement prepped = connection.prepareStatement("INSERT INTO occupancy VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, row(?, ?, ?))");
		int paramIndex = 1;
		prepped.setLong(paramIndex++, server.getTickCount());
		prepped.setObject(paramIndex++, zone.home().id());
		prepped.setString(paramIndex++, zone.name());
		prepped.setString(paramIndex++, livingEntity.getName().getString());
		prepped.setDouble(paramIndex++, livingEntity.getX());
		prepped.setDouble(paramIndex++, livingEntity.getY());
		prepped.setDouble(paramIndex++, livingEntity.getZ());
		prepped.execute();
		prepped.close();
	}
}
