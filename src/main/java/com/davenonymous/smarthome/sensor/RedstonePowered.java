package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.setup.content.ModBlocks;
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
import java.sql.SQLException;
import java.sql.Statement;

@SmartHomeSensor(modid = "minecraft")
public class RedstonePowered implements ISensor {
	public static final ResourceLocation ID = SmartHome.resource("sensor/redstone");

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return !state.isAir();
	}

	@Override
	public void createTables(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS redstone (instant TIMESTAMP, tick UBIGINT, home UUID, zone VARCHAR, power UTINYINT, pos STRUCT(x BIGINT, y BIGINT, z BIGINT))");
		stmt.close();
	}

	@Override
	public void visitZoneBlock(DuckDBConnection connection, ServerLevel level, HomeZone zone, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {

		int signal = 0;
		if(state.hasAnalogOutputSignal()) {
			signal = state.getAnalogOutputSignal(level, pos);
		} else if(state.isRedstoneConductor(level, pos)) {
			signal = level.getBestNeighborSignal(pos);
		} else if(level.hasNeighborSignal(pos)){
			signal = level.getBestNeighborSignal(pos);
		}

		PreparedStatement prepped = connection.prepareStatement("INSERT INTO redstone VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, row(?, ?, ?))");
		int paramIndex = 1;
		prepped.setLong(paramIndex++, level.getServer().getTickCount());
		prepped.setObject(paramIndex++, zone.home().id());
		prepped.setString(paramIndex++, zone.name());
		prepped.setInt(paramIndex++, signal);
		prepped.setInt(paramIndex++, pos.getX());
		prepped.setInt(paramIndex++, pos.getY());
		prepped.setInt(paramIndex++, pos.getZ());
		prepped.execute();
		prepped.close();
	}
}
