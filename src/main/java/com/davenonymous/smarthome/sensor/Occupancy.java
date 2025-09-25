package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@SmartHomeSensor(modid = "minecraft")
public class Occupancy implements ISensor {
	public static final ResourceLocation ID = SmartHome.resource("sensor/occupancy");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return state.is(ModBlocks.DASHBOARD);
	}

	@Override
	public void createTables(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS occupancy (instant TIMESTAMP, tick LONG, home UUID, zone VARCHAR, visitor VARCHAR, pos STRUCT(x DOUBLE, y DOUBLE, z DOUBLE))");
		stmt.close();
	}

	@Override
	public void visitZoneEntity(DuckDBConnection connection, ServerLevel level, HomeZone zone, ConfiguredDevice device, Entity entity) throws SQLException {
		if(!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		PreparedStatement prepped = connection.prepareStatement("INSERT INTO occupancy VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, row(?, ?, ?))");
		int paramIndex = 1;
		prepped.setLong(paramIndex++, level.getServer().getTickCount());
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
