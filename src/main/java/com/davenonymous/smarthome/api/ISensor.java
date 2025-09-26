package com.davenonymous.smarthome.api;

import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.machinezoo.noexception.throwing.ThrowingFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ISensor<T extends SensorSettings> {
	ResourceLocation id();

	boolean isValid(Level level, BlockPos pos, BlockState state);

	T getDefaultSettings();

	default boolean isGeneric() {
		return false;
	}

	default boolean shouldVisitAllBlocksInZone() {
		return false;
	}

	default String nameTranslationKey() {
		var dotted = id().getPath().replaceAll("/", ".");
		return id().getNamespace() + "." + dotted + ".name";
	}

	default String descriptionTranslationKey() {
		var dotted = id().getPath().replaceAll("/", ".");
		return id().getNamespace() + "." + dotted + ".description";
	}

	String getTableName();

	default void createTable(DuckDBConnection connection) throws SQLException {}

	default ThrowingFunction<DuckDBConnection, ResultSet> stateForDevice(HomeZone zone, ConfiguredDevice device) {
		return connection -> {
			PreparedStatement prepped = connection.prepareStatement("SELECT * FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? ORDER BY tick DESC LIMIT 1");
			int paramIndex = 1;
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			return prepped.executeQuery();
		};
	}

	default ThrowingFunction<DuckDBConnection, ResultSet> historyForDevice(HomeZone zone, ConfiguredDevice device, long start, long end) {
		return connection -> {
			PreparedStatement prepped = connection.prepareStatement("SELECT * FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? AND tick > ? AND tick <= ? ORDER BY tick DESC");
			int paramIndex = 1;
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			prepped.setLong(paramIndex++, start);
			prepped.setLong(paramIndex++, end);
			return prepped.executeQuery();
		};
	}

	ThrowingConsumer<DuckDBConnection> NOOP = (connection) -> {};

	default ThrowingConsumer<DuckDBConnection> visitHome(ServerLevel server, HomeCore home, ConfiguredDevice device) throws SQLException {
		return NOOP;
	}
	default ThrowingConsumer<DuckDBConnection> visitZone(ServerLevel server, HomeZone zone, ConfiguredDevice device) throws SQLException {
		return NOOP;
	}
	default ThrowingConsumer<DuckDBConnection> visitZoneEntity(ServerLevel server, HomeZone zone, ConfiguredDevice device, Entity entity) throws SQLException {
		return NOOP;
	}
	default ThrowingConsumer<DuckDBConnection> visitZoneBlock(ServerLevel server, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		return NOOP;
	}

}
