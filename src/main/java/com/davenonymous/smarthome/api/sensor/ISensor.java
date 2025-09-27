package com.davenonymous.smarthome.api.sensor;

import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
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
import java.util.HashMap;
import java.util.Map;

public interface ISensor<T extends SensorSettings, U extends ISensorData> {
	ResourceLocation id();

	boolean isValid(Level level, BlockPos pos, BlockState state);

	T getDefaultSettings();

	Class<U> getDataClass();

	String getTableName();

	U getStateFromResultSet(ResultSet resultSet) throws SQLException;

	boolean supportsVisualization(IVisualization<?, ?> visualization);

	default <S extends IVisualizationSettings, V extends IVisualization<D, S>, D extends IVisualizationData> D getVisualizationData(HomeZone zone, ConfiguredDevice device, T sensorSettings, IVisualization<D, S> visualization, S visualizationSettings) {
		return null;
	}

	default Map<Long, U> getHistoryFromResultSet(ResultSet resultSet)  {
		Map<Long, U> results = new HashMap<>();
		try {
			while(resultSet.next()) {
				long tick = resultSet.getLong("tick");
				var state = getStateFromResultSet(resultSet);

				results.put(tick, state);
			}
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return results;
	}

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

	default void createTable(DuckDBConnection connection) throws SQLException {}

	default ThrowingFunction<DuckDBConnection, ResultSet> stateForDevice(HomeZone zone, ConfiguredDevice device) {
		return connection -> {
			PreparedStatement prepped = connection.prepareStatement("SELECT * FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? ORDER BY instant DESC LIMIT 1");
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
