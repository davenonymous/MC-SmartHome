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
import com.davenonymous.smarthome.setup.content.ModVisualizations;
import com.davenonymous.smarthome.visualization.gauge.GaugeViz;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizData;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizSettings;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SmartHomeSensor(modid = "minecraft")
public class EnergyStorage implements ISensor<EnergyStorageSettings, EnergyStorageData> {
	public static final ResourceLocation ID = SmartHome.resource("sensor/forge_energy_storage");
	public static final BlockCapability<IEnergyStorage, @Nullable Direction> ENERGY = Capabilities.EnergyStorage.BLOCK;

	@Override
	public Class<EnergyStorageData> getDataClass() {
		return EnergyStorageData.class;
	}

	@Override
	public EnergyStorageSettings getDefaultSettings() {
		return new EnergyStorageSettings(Optional.empty());
	}

	@Override
	public ResourceLocation getDefaultVisualization() {
		return GaugeViz.ID;
	}

	@Override
	public IVisualizationSettings getDefaultVisualizationSettings() {
		return new GaugeVizSettings(0, 100);
	}

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
					PreparedStatement prepped = connection.prepareStatement("SELECT energy FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? ORDER BY instant DESC LIMIT 1");
					int paramIndex = 0;
					prepped.setObject(paramIndex++, zone.home().id());
					prepped.setObject(paramIndex++, zone.id());
					prepped.setObject(paramIndex++, device.id());
					var resultSet = prepped.executeQuery();
					if(resultSet.next()) {
						long energy = resultSet.getLong("energy");
						return new GaugeVizData((double)energy);
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
	public EnergyStorageData getStateFromResultSet(ResultSet resultSet) throws SQLException{
		long energy = resultSet.getLong("energy");
		long max = resultSet.getLong("max");
		return new EnergyStorageData(energy, max);
	}

	@Override
	public void createTable(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS forge_energy_storage (instant TIMESTAMP, tick UBIGINT, home UUID, zone UUID, device UUID, energy UBIGINT, max UBIGINT, pos STRUCT(x BIGINT, y BIGINT, z BIGINT))");
		stmt.close();
	}

	@Override
	public Consumer<DuckDBConnection> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		IEnergyStorage cap = level.getCapability(ENERGY, pos, null);
		if(cap == null) {
			return NOOP;
		}

		long stored = cap.getEnergyStored();
		long max = cap.getMaxEnergyStored();

		return connection -> {
			try {
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
			} catch (SQLException e) {
				SmartHome.LOGGER.error("Failed to record energy storage data for device {}", device.id(), e);
			}
		};
	}
}
