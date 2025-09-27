package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensor;
import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.sensor.SmartHomeSensor;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.visualization.gauge.GaugeViz;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizData;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizSettings;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.function.Function;

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
	public boolean supportsVisualization(IVisualization<?, ?> visualization) {
		if(visualization instanceof GaugeViz) {
			return true;
		}

		return false;
	}

	@Override
	public Function<DuckDBConnection, IVisualizationData> getVisualizationData(HomeZone zone, ConfiguredDevice device, SensorSettings sensorSettings, IVisualization<?, ?> visualization, IVisualizationSettings visualizationSettings) {
		return connection -> {
			if(visualization instanceof GaugeViz gaugeViz && visualizationSettings instanceof GaugeVizSettings gaugeSettings) {

				try {
					PreparedStatement prepped = connection.prepareStatement("SELECT power FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? ORDER BY instant DESC LIMIT 1");
					int paramIndex = 1;
					prepped.setObject(paramIndex++, zone.home().id());
					prepped.setObject(paramIndex++, zone.id());
					prepped.setObject(paramIndex++, device.id());
					var resultSet = prepped.executeQuery();
					if(resultSet.next()) {
						int power = resultSet.getInt("power");
						return new GaugeVizData(power);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}

				return null;
			}
			return null;
		};
	}

	@Override
	public RedstonePoweredData getStateFromResultSet(ResultSet resultSet) throws SQLException {
		return new RedstonePoweredData(resultSet.getInt("power"));
	}

	@Override
	public RedstonePoweredSettings getDefaultSettings() {
		return new RedstonePoweredSettings(false);
	}

	@Override
	public void createTable(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS redstone (instant TIMESTAMP, tick UBIGINT, home UUID, zone UUID, device UUID, power UTINYINT, pos STRUCT(x BIGINT, y BIGINT, z BIGINT))");
		stmt.close();
	}

	@Override
	public Consumer<DuckDBConnection> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {

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
			try {
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
			} catch (SQLException e) {
				SmartHome.LOGGER.error("Failed to record redstone data for device {}", device.id(), e);
			}
		};
	}
}
