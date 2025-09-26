package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.machinezoo.noexception.throwing.ThrowingFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.duckdb.DuckDBConnection;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SmartHomeSensor(modid = "minecraft")
public class EnergyStorage implements ISensor {
	public static final ResourceLocation ID = SmartHome.resource("sensor/forge_energy_storage");
	public static final BlockCapability<IEnergyStorage, @Nullable Direction> ENERGY = Capabilities.EnergyStorage.BLOCK;

	@Override
	public String getTableName() {
		return "forge_energy_storage";
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return level.getCapability(ENERGY, pos, null) != null;
	}

	@Override
	public void createTable(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS forge_energy_storage (instant TIMESTAMP, tick UBIGINT, home UUID, zone UUID, device UUID, energy UBIGINT, max UBIGINT, pos STRUCT(x BIGINT, y BIGINT, z BIGINT))");
		stmt.close();
	}

	@Override
	public ThrowingConsumer<DuckDBConnection> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		IEnergyStorage cap = level.getCapability(ENERGY, pos, null);
		if(cap == null) {
			return NOOP;
		}

		long stored = cap.getEnergyStored();
		long max = cap.getMaxEnergyStored();

		return connection -> {
			PreparedStatement prepped = connection.prepareStatement("INSERT INTO forge_energy_storage VALUES (CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, row(?, ?, ?))");
			int paramIndex = 1;
			prepped.setLong(paramIndex++, level.getServer().getTickCount());
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			prepped.setLong(paramIndex++, stored);
			prepped.setLong(paramIndex++, max);
			prepped.setInt(paramIndex++, pos.getX());
			prepped.setInt(paramIndex++, pos.getY());
			prepped.setInt(paramIndex++, pos.getZ());
			prepped.execute();
			prepped.close();
		};
	}
}
