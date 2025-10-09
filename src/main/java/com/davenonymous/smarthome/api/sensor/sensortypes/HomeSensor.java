package com.davenonymous.smarthome.api.sensor.sensortypes;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.SensorRange;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.api.sensor.DBHandler;
import com.davenonymous.smarthome.sensor.energy.EnergyStorageData;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.api.sensor.SensorColumn;
import com.davenonymous.smarthome.visualization.line.LineViz;
import com.davenonymous.smarthome.visualization.line.LineVizSeriesSettings;
import com.davenonymous.smarthome.visualization.line.LineVizSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.duckdb.DuckDBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public interface HomeSensor<D extends ISensorData, T extends SensorSettings> {

	boolean isValid(Level level, BlockPos pos, BlockState state);

	default ResourceLocation getDefaultVisualization() {
		return LineViz.ID;
	}

	default IVisualizationSettings getDefaultVisualizationSettings(ConfiguredDevice device) {
		var colors = IVisualizationSettings.defaultColors();
		Map<String, LineVizSeriesSettings> result = new HashMap<>();

		for(var column : getColumns()) {
			if(!column.type().isNumeric()) {
				continue;
			}

			var series = new LineVizSeriesSettings(true, colors.next(), column.label().get());
			result.put(column.name(), series);
		}

		return new LineVizSettings(Map.of(device.id(), result));
	}

	default boolean isGeneric() {
		return false;
	}

	default boolean isMultiRow() {
		return false;
	}

	default SensorRange getRange() {
		return new SensorRange.None();
	}

	default ResourceLocation id() {
		return ModSensors.ID_BY_CLASS.get(this.getClass());
	}

	default I18String getDisplayName() {
		return ModSensors.NAME_BY_CLASS.get(this.getClass());
	}

	default I18String getDescription() {
		return ModSensors.DESCRIPTION_BY_CLASS.get(this.getClass());
	}

	default T getDefaultSettings() {
		//noinspection unchecked
		return (T) ModSensors.DEFAULT_SETTINGS_BY_CLASS.get(this.getClass());
	}

	default DBHandler<D, ?> getDBHandler() {
		//noinspection unchecked
		return (DBHandler<D, ?>) ModSensors.DB_HANDLERS.get(this.id());
	}

	default List<SensorColumn> getColumns() {
		return ModSensors.SENSOR_COLUMNS.getOrDefault(id(), List.of());
	}

	default SensorColumn getColumn(int index) {
		List<SensorColumn> sensorList = ModSensors.SENSOR_COLUMNS.getOrDefault(id(), List.of());
		if(index < 0 || index >= sensorList.size()) {
			throw new IndexOutOfBoundsException("Column index " + index + " is out of bounds for sensor " + id() + " with " + sensorList.size() + " columns");
		}
		return sensorList.get(index);
	}

	default SensorColumn getColumn(String name) {
		List<SensorColumn> sensorList = ModSensors.SENSOR_COLUMNS.getOrDefault(id(), List.of());
		return sensorList.stream().filter(c -> c.name().equals(name)).findFirst().orElse(null);
	}

	default ResourceLocation getTableName() {
		var noSlashPath = id().getPath().replace('/', '_');
		return ResourceLocation.fromNamespaceAndPath(id().getNamespace(), noSlashPath);
	}

	default Optional<SensorColumn> getDefaultColumn() {
		return getColumns().isEmpty() ? Optional.empty() : Optional.of(getColumns().getFirst());
	}

	Consumer<DuckDBConnection> NOOP = (connection) -> {};

	static <T> T cast(SensorSettings settings) {
		return (T)settings;
	}

	static <D> D cast(ISensorData data) {
		return (D)data;
	}

	default double valueFromData(D data, SensorColumn column) {
		if(!column.type().isNumeric()) {
			throw new IllegalArgumentException("Column " + column.name() + " is not numeric");
		}
		if(data.columnValues().length <= column.index()) {
			throw new IllegalArgumentException("Column index " + column.index() + " is out of bounds for data with " + data.columnValues().length + " columns");
		}

		var num = (Number) data.columnValues()[column.index()];
		return num.doubleValue();
	}

	D dataFromResultSet(ResultSet resultSet) throws SQLException;
}
