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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SmartHomeSensor(modid = "minecraft")
public class RedstonePowered implements ISensor<RedstonePoweredSettings, RedstonePoweredData> {
	public static final ResourceLocation ID = SmartHome.resource("sensor/redstone");

	@Override
	public Class<RedstonePoweredData> getDataClass() {
		return RedstonePoweredData.class;
	}

	@Override
	public String getTableName() {
		return "redstone";
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean isGeneric() {
		return true;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return !state.isAir();
	}

	@Override
	public RedstonePoweredData getStateFromResultSet(ResultSet resultSet) throws SQLException {
		return new RedstonePoweredData(resultSet.getInt("power"));
	}

	@Override
	public RedstonePoweredSettings getDefaultSettings() {
		return new RedstonePoweredSettings();
	}

	@Override
	public void createTable(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS redstone (instant TIMESTAMP, tick UBIGINT, home UUID, zone UUID, device UUID, power UTINYINT, pos STRUCT(x BIGINT, y BIGINT, z BIGINT))");
		stmt.close();
	}

	@Override
	public ThrowingConsumer<DuckDBConnection> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {

		int signal;
		if(state.hasAnalogOutputSignal()) {
			signal = state.getAnalogOutputSignal(level, pos);
		} else if(state.isRedstoneConductor(level, pos)) {
			signal = level.getBestNeighborSignal(pos);
		} else if(level.hasNeighborSignal(pos)){
			signal = level.getBestNeighborSignal(pos);
		} else {
			signal = 0;
		}

		return connection -> {
			PreparedStatement prepped = connection.prepareStatement("INSERT INTO redstone VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, row(?, ?, ?))");
			int paramIndex = 1;
			prepped.setLong(paramIndex++, level.getServer().getTickCount());
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			prepped.setInt(paramIndex++, signal);
			prepped.setInt(paramIndex++, pos.getX());
			prepped.setInt(paramIndex++, pos.getY());
			prepped.setInt(paramIndex++, pos.getZ());
			prepped.execute();
			prepped.close();
		};
	}
}
