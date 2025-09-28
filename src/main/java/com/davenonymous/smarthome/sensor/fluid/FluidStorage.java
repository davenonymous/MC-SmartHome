package com.davenonymous.smarthome.sensor.fluid;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensor;
import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.api.sensor.SmartHomeSensor;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.visualization.gauge.GaugeViz;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizData;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizSettings;
import com.davenonymous.smarthome.visualization.line.LineViz;
import com.davenonymous.smarthome.visualization.line.LineVizData;
import com.davenonymous.smarthome.visualization.line.LineVizSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.duckdb.DuckDBConnection;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@SmartHomeSensor(modid = "minecraft")
public class FluidStorage implements ISensor<FluidStorageSettings, FluidStorageData> {
	public static final ResourceLocation ID = SmartHome.resource("sensor/fluid_storage");
	public static final BlockCapability<IFluidHandler, @Nullable Direction> FLUID = Capabilities.FluidHandler.BLOCK;

	@Override
	public Class<FluidStorageData> getDataClass() {
		return FluidStorageData.class;
	}

	@Override
	public FluidStorageSettings getDefaultSettings() {
		return new FluidStorageSettings(false, Optional.empty());
	}

	@Override
	public ResourceLocation getDefaultVisualization() {
		return LineViz.ID;
	}

	@Override
	public IVisualizationSettings getDefaultVisualizationSettings() {
		return new LineVizSettings(List.of(
			ColorHelper.COLOR_ORANGE, ColorHelper.COLOR_PURPLE, ColorHelper.COLOR_CYAN, ColorHelper.COLOR_GREEN, ChatFormatting.BLUE.getColor()
		));
	}

	@Override
	public String getTableName() {
		return "fluid_storage";
	}

	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return level.getCapability(FLUID, pos, null) != null;
	}

	@Override
	public boolean supportsVisualization(IVisualization<?, ?> visualization) {
		if(visualization instanceof GaugeViz)return true;
		if(visualization instanceof LineViz)return true;

		return false;
	}

	@Override
	public Function<DuckDBConnection, IVisualizationData> getVisualizationData(HomeZone zone, ConfiguredDevice device, SensorSettings sensorSettings, IVisualization<?, ?> visualization, IVisualizationSettings visualizationSettings) {
		return connection -> {
			if(visualization instanceof GaugeViz && visualizationSettings instanceof GaugeVizSettings) {
				return getGaugeVizData(zone, device, connection);
			} else if(visualization instanceof LineViz && visualizationSettings instanceof LineVizSettings) {
				return getLineVizData(zone, device, connection);
			}
			return null;
		};
	}

	private @Nullable LineVizData getLineVizData(HomeZone zone, ConfiguredDevice device, DuckDBConnection connection) {
		try {
			PreparedStatement prepped = connection.prepareStatement("SELECT tick, stored FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? ORDER BY instant DESC LIMIT 2000");
			int paramIndex = 1;
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			var resultSet = prepped.executeQuery();

			Map<Long, Double> series = new LinkedHashMap<>();
			while(resultSet.next()) {
				long tick = resultSet.getLong("tick");
				long stored = resultSet.getLong("stored");
				series.put(tick, (double) stored);
			}

			return new LineVizData(List.of(series));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	private @Nullable GaugeVizData getGaugeVizData(HomeZone zone, ConfiguredDevice device, DuckDBConnection connection) {
		try {
			PreparedStatement prepped = connection.prepareStatement("SELECT stored FROM " + getTableName() + " WHERE home = ? AND zone = ? AND device = ? ORDER BY instant DESC LIMIT 1");
			int paramIndex = 1;
			prepped.setObject(paramIndex++, zone.home().id());
			prepped.setObject(paramIndex++, zone.id());
			prepped.setObject(paramIndex++, device.id());
			var resultSet = prepped.executeQuery();
			if(resultSet.next()) {
				long stored = resultSet.getLong("stored");
				return new GaugeVizData((double) stored);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public FluidStorageData getStateFromResultSet(ResultSet resultSet) throws SQLException{
		ResourceLocation location = ResourceLocation.parse(resultSet.getString("type"));
		long stored = resultSet.getLong("stored");
		long capacity = resultSet.getLong("capacity");
		return new FluidStorageData(location, stored, capacity);
	}

	@Override
	public void createTable(DuckDBConnection connection) throws SQLException {
		Statement stmt = connection.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS fluid_storage (instant TIMESTAMP, tick UBIGINT, home UUID, zone UUID, device UUID, tank INT, type VARCHAR, stored UBIGINT, capacity UBIGINT, pos STRUCT(x BIGINT, y BIGINT, z BIGINT))");
		stmt.close();
	}

	@Override
	public Consumer<DuckDBConnection> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		IFluidHandler cap = level.getCapability(FLUID, pos, null);
		if(cap == null) {
			return NOOP;
		}

		List<Consumer<DuckDBConnection>> records = new ArrayList<>();
		for(int tank = 0; tank < cap.getTanks(); tank++) {
			FluidStack fluidInTank = cap.getFluidInTank(tank);
			if(!fluidInTank.isEmpty()) {
				var fluidName = fluidInTank.getFluid().getFluidType().getDescriptionId(fluidInTank);
				records.add(recordFluid(level, zone, device, pos, tank, fluidName, fluidInTank.getAmount(), cap.getTankCapacity(tank)));
			}
		}

		return connection -> {
			records.forEach(r -> r.accept(connection));
		};
	}

	private Consumer<DuckDBConnection> recordFluid(ServerLevel level, HomeZone zone, ConfiguredDevice device, BlockPos pos, int tank, String fluidType, long stored, long capacity) {
		return connection -> {
			try {
				PreparedStatement prepped = connection.prepareStatement("INSERT INTO fluid_storage VALUES ( CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, ?, ?, row(?, ?, ?) )");
				int paramIndex = 1;
				prepped.setLong(paramIndex++, level.getServer().getTickCount());

				prepped.setObject(paramIndex++, zone.home().id());
				prepped.setObject(paramIndex++, zone.id());
				prepped.setObject(paramIndex++, device.id());

				prepped.setInt(paramIndex++, tank);
				prepped.setString(paramIndex++, fluidType);
				prepped.setLong(paramIndex++, stored);
				prepped.setLong(paramIndex++, capacity);

				prepped.setInt(paramIndex++, pos.getX());
				prepped.setInt(paramIndex++, pos.getY());
				prepped.setInt(paramIndex++, pos.getZ());
				prepped.execute();
				prepped.close();
			} catch (SQLException e) {
				SmartHome.LOGGER.error("Failed to record fluid storage data for device {}", device.id(), e);
			}
		};
	}
}
