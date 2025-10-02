package com.davenonymous.smarthome.api.sensor.sensortypes;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface HomeSensor<D extends ISensorData, T extends SensorSettings> {

	boolean isValid(Level level, BlockPos pos, BlockState state);

	default ResourceLocation getDefaultVisualization() {
		return LineViz.ID;
	}

	default IVisualizationSettings getDefaultVisualizationSettings() {
		var colors = IVisualizationSettings.defaultColors();
		List<LineVizSeriesSettings> seriesSettings = new ArrayList<>();
		for(var column : getColumns()) {
			if(!column.type().isNumeric()) {
				continue;
			}

			var series = new LineVizSeriesSettings(colors.next(), column.translationKey());
			seriesSettings.add(series);
		}

		return new LineVizSettings(seriesSettings);
	}

	default boolean isGeneric() {
		return false;
	}

	default boolean isMultiRow() {
		return false;
	}

	default ResourceLocation id() {
		return ModSensors.ID_BY_CLASS.get(this.getClass());
	}

	default I18String getDisplayName() {
		return ModSensors.NAME_BY_CLASS.get(this.getClass());
	}

	default I18String getDescription() {
		// TODO: implement description annotation
		return ModSensors.NAME_BY_CLASS.get(this.getClass());
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

	double valueFromData(D data, SensorColumn column);

	D dataFromResultSet(ResultSet resultSet) throws SQLException;
}
