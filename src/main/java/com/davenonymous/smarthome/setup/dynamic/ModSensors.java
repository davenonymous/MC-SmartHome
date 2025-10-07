package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.DBHandler;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.SensorColumn;
import com.davenonymous.smarthome.api.sensor.SensorColumnType;
import com.davenonymous.smarthome.api.sensor.annotations.*;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.sensor.SensorDataCodecRegistry;
import com.davenonymous.smarthome.sensor.SensorSettingsCodecRegistry;
import com.davenonymous.smarthome.sensor.annotation.SensorDataColumnLabel;
import com.davenonymous.smarthome.util.AnnotationHelpers;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.duckdb.DuckDBConnection;

import java.lang.annotation.ElementType;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("rawtypes")
public class ModSensors {
	public static Map<ResourceLocation, HomeSensor<?, ?>> SENSORS = new HashMap<>();
	public static Map<ResourceLocation, DBHandler<?, ?>> DB_HANDLERS = new HashMap<>();
	public static Map<ResourceLocation, List<SensorColumn>> SENSOR_COLUMNS = new HashMap<>();

	public static Map<Class<?>, ResourceLocation> ID_BY_CLASS = new HashMap<>();
	public static Map<Class<?>, I18String> NAME_BY_CLASS = new HashMap<>();

	public static Map<Class<?>, MapCodec> SETTINGS_CODEC_BY_CLASS = new HashMap<>();
	public static Map<Class<?>, StreamCodec> SETTINGS_STREAM_CODEC_BY_CLASS = new HashMap<>();
	public static Map<Class<?>, StreamCodec> DATA_STREAM_CODEC_BY_CLASS = new HashMap<>();
	public static Map<Class<?>, SensorSettings> DEFAULT_SETTINGS_BY_CLASS = new HashMap<>();

	public static HomeSensor<?, ?> getById(ResourceLocation id) {
		return SENSORS.get(id);
	}

	public static void find() {
		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var sensorClassAnnotations = scanData.getAnnotatedBy(SmartHomeSensor.class, ElementType.TYPE);

		Set<String> registeredSettingsCodecs = new HashSet<>();
		Set<String> registeredDataCodecs = new HashSet<>();
		sensorClassAnnotations.forEach(annotationData -> {
			var rawAnnotationData = annotationData.annotationData();
			Object modIdAnnotation = rawAnnotationData.get("modid");
			if(!(modIdAnnotation instanceof String modid)) {
				return;
			}

			if(!ModList.get().isLoaded(modid)) {
				return;
			}

			Object sensorDataAnnotation = rawAnnotationData.get("data");
			Class<ISensorData> dataClazz = AnnotationHelpers.getClassFromAnnotationData(sensorDataAnnotation, ISensorData.class);

			Object sensorSettingsAnnotation = rawAnnotationData.get("settings");
			Class<SensorSettings> settingsClazz = AnnotationHelpers.getClassFromAnnotationData(sensorSettingsAnnotation, SensorSettings.class);

			Class<HomeSensor<?, ?>> sensorClazz = AnnotationHelpers.getAnnotatedClass(annotationData);

			ResourceLocation sensorId = AnnotationHelpers.getSingularFieldData(sensorClazz, SensorId.class, ResourceLocation.class);
			I18String sensorName = AnnotationHelpers.getSingularFieldData(sensorClazz, SensorName.class, I18String.class);
			SensorSettings defaultSettings = AnnotationHelpers.getSingularFieldData(settingsClazz, SensorSettingsDefault.class, SensorSettings.class);

			MapCodec settingsCodec = AnnotationHelpers.getSingularFieldData(settingsClazz, SensorSettingsCodec.class, MapCodec.class);
			StreamCodec settingsStreamCodec = AnnotationHelpers.getSingularFieldData(settingsClazz, SensorSettingsStreamCodec.class, StreamCodec.class);
			StreamCodec dataStreamCodec = AnnotationHelpers.getSingularFieldData(dataClazz, SensorDataStreamCodec.class, StreamCodec.class);

			var sensorClassSimpleName = sensorClazz.getSimpleName();
			var sensorClassSnakeCaseName = snakeCase(sensorClassSimpleName);

			var settingsClassSimpleName = settingsClazz.getSimpleName();
			var settingsClassSnakeCaseName = snakeCase(settingsClassSimpleName);

			Map<String, I18String> columnLabels = new HashMap<>();
			for(var field : dataClazz.getDeclaredFields()) {
				if(!Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				if(!field.isAnnotationPresent(SensorDataColumnLabel.class)) {
					continue;
				}

				if(!I18String.class.isAssignableFrom(field.getType())) {
					SmartHome.LOGGER.error("Sensor class {} has @SensorDataColumnLabel, but not on type I18String: {} {}", annotationData.clazz().getClassName(), field.getName(), field.getType().getName());
					return;
				}

				var columnLabelAnnotation = field.getAnnotation(SensorDataColumnLabel.class);
				var columnNameForLabel = columnLabelAnnotation.value();
				try {
					I18String columnLabel = (I18String) field.get(null);
					columnLabels.put(columnNameForLabel, columnLabel);
				} catch (IllegalAccessException e) {
				}
			}

			List<SensorColumn> columns = new ArrayList<>();
			int colIndex = 0;
			for(var field : dataClazz.getDeclaredFields()) {
				if(Modifier.isStatic(field.getModifiers())) {
					continue;
				}

				var columnType = SensorColumnType.byValueClass(field.getType());
				if(columnType == null) {
					throw new RuntimeException("Sensor class " + annotationData.clazz().getClassName() + " has data class with field of unsupported type: " + field.getName() + " " + field.getType().getName());
				}

				var columnLabel = columnLabels.get(field.getName());
				if(columnLabel == null) {
					throw new RuntimeException("Sensor class " + annotationData.clazz().getClassName() + " has data class with field without @SensorDataColumnLabel: " + field.getName() + " " + field.getType().getName());
				}

				var snakeCaseName = snakeCase(field.getName());
				var column = new SensorColumn(colIndex++, snakeCaseName, columnLabel, columnType);
				columns.add(column);
			}

			try {
				HomeSensor<?, ?> sensor = sensorClazz.getDeclaredConstructor().newInstance();
				SENSORS.put(sensorId, sensor);
				DB_HANDLERS.put(sensorId, new DBHandler<>(sensor));
				ID_BY_CLASS.put(sensorClazz, sensorId);
				NAME_BY_CLASS.put(sensorClazz, sensorName);

				if(!registeredSettingsCodecs.contains(settingsClassSnakeCaseName)) {
					SmartHome.LOGGER.info("Registering sensor settings codec for {} as {}", settingsClazz.getSimpleName(), settingsClassSnakeCaseName);
					SETTINGS_CODEC_BY_CLASS.put(settingsClazz, settingsCodec);
					//noinspection unchecked
					SensorSettingsCodecRegistry.DEFERRED_SENSOR_SETTINGS.register(settingsClassSnakeCaseName, () -> settingsCodec);

					SETTINGS_STREAM_CODEC_BY_CLASS.put(settingsClazz, settingsStreamCodec);
					//noinspection unchecked
					SensorSettingsCodecRegistry.DEFERRED_SENSOR_SETTINGS_DISPATCHER.register(settingsClassSnakeCaseName, () -> settingsStreamCodec);

					registeredSettingsCodecs.add(settingsClassSnakeCaseName);
				}

				DATA_STREAM_CODEC_BY_CLASS.put(dataClazz, dataStreamCodec);
				if(!registeredDataCodecs.contains(sensorClassSnakeCaseName)) {
					SmartHome.LOGGER.info("Registering sensor data codec for {} as {}", dataClazz.getSimpleName(), sensorClassSnakeCaseName);
					//noinspection unchecked
					SensorDataCodecRegistry.DEFERRED_SENSOR_DATA_DISPATCHER.register(sensorClassSnakeCaseName, () -> dataStreamCodec);
					registeredDataCodecs.add(sensorClassSnakeCaseName);
				}

				DEFAULT_SETTINGS_BY_CLASS.put(sensorClazz, defaultSettings);
				DEFAULT_SETTINGS_BY_CLASS.put(settingsClazz, defaultSettings);

				SETTINGS_CODEC_BY_CLASS.put(sensorClazz, settingsCodec);
				SETTINGS_STREAM_CODEC_BY_CLASS.put(sensorClazz, settingsStreamCodec);
				DATA_STREAM_CODEC_BY_CLASS.put(sensorClazz, dataStreamCodec);
				DEFAULT_SETTINGS_BY_CLASS.put(sensorClazz, defaultSettings);


				SmartHome.LOGGER.info("Found sensor: {} (mod: {})", sensorId, modid);
				for(var col : columns) {
					SmartHome.LOGGER.info("  Column: {} Type: {}", col.name(), col.type().sqlType());
				}
				SENSOR_COLUMNS.put(sensorId, columns);
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to instantiate sensor class: {}", annotationData.clazz().getClassName(), e);
			}
		});
	}

	private static String snakeCase(String input) {
		return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
	}

	public static List<HomeSensor<?, ?>> getValidSensors(Level level, BlockPos pos, BlockState state) {
		List<HomeSensor<?, ?>> validSensors = new ArrayList<>();
		for(var sensor : SENSORS.values()) {
			if(sensor.isValid(level, pos, state)) {
				validSensors.add(sensor);
			}
		}
		return validSensors;
	}

	public static void createTables(DuckDBConnection connection) throws SQLException {
		for(var handler : DB_HANDLERS.values()) {
			handler.createTable(connection);
		}
	}
}
