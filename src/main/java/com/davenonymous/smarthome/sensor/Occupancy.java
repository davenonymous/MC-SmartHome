package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SensorSettings;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@SmartHomeSensor(modid = "minecraft")
public class Occupancy implements ISensor<OccupancySettings, OccupancyData> {
	public static final ResourceLocation ID = SmartHome.resource("sensor/occupancy");

	@Override
	public String getTableName() {
		return "occupancy";
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return state.is(ModBlocks.DASHBOARD);
	}

	@Override
	public OccupancyData getStateFromResultSet(ResultSet resultSet) throws SQLException {
		// TODO: implement me
		return new OccupancyData(List.of());
	}

	@Override
	public OccupancySettings getDefaultSettings() {
		return new OccupancySettings();
	}

	@Override
	public void createTable(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS occupancy (instant TIMESTAMP, tick LONG, home UUID, zone UUID, device UUID, visitor STRUCT(id INT, name VARCHAR, type VARCHAR, category VARCHAR), pos STRUCT(x DOUBLE, y DOUBLE, z DOUBLE))");
		stmt.close();
	}

	@Override
	public ThrowingConsumer<DuckDBConnection> visitZoneEntity(ServerLevel level, HomeZone zone, ConfiguredDevice device, Entity entity) throws SQLException {
		if(!(entity instanceof LivingEntity livingEntity)) {
			return NOOP;
		}

		var name = livingEntity.getName().getString();
		var type = livingEntity.getType().getDescriptionId();
		var category = livingEntity.getClassification(false).getName();
		var entityId = livingEntity.getId();

		return connection -> {
			PreparedStatement prepped = connection.prepareStatement("INSERT INTO occupancy VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, row(?, ?, ?, ?), row(?, ?, ?))");
			int paramIndex = 1;
			prepped.setLong(paramIndex++, level.getServer().getTickCount());
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			prepped.setInt(paramIndex++, entityId);
			prepped.setString(paramIndex++, name);
			prepped.setString(paramIndex++, type);
			prepped.setString(paramIndex++, category);
			prepped.setDouble(paramIndex++, livingEntity.getX());
			prepped.setDouble(paramIndex++, livingEntity.getY());
			prepped.setDouble(paramIndex++, livingEntity.getZ());
			prepped.execute();
			prepped.close();
		};
	}
}
