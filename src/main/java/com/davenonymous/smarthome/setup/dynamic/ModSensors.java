package com.davenonymous.smarthome.setup.dynamic;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensor;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.api.sensor.SmartHomeSensor;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.duckdb.DuckDBConnection;

import java.lang.annotation.ElementType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModSensors {
	public static Map<ResourceLocation, ISensor<?, ?>> SENSORS = new HashMap<>();

	public static <T extends SensorSettings> ISensor<T, ?> getBySettings(T settings) {
		for(var sensor : SENSORS.values()) {
			if(sensor.getDefaultSettings().getClass() == settings.getClass()) {
				//noinspection unchecked
				return (ISensor<T, ?>) sensor;
			}
		}

		return null;
	}

	public static <T extends ISensorData> ISensor<?, T> getByData(T data) {
		// TODO: This is non-sense, we should have sensor IDs or a registry or something
		for(var sensor : SENSORS.values()) {
			if(sensor.getDataClass() == data.getClass()) {
				//noinspection unchecked
				return (ISensor<?, T>) sensor;
			}
		}

		return null;
	}

	public static ISensor<?, ?> getById(ResourceLocation id) {
		return SENSORS.get(id);
	}

	public static List<ISensor<?, ?>> getValidSensors(Level level, BlockPos pos, BlockState state) {
		List<ISensor<?, ?>> validSensors = new ArrayList<>();
		for(var sensor : SENSORS.values()) {
			if(sensor.isValid(level, pos, state)) {
				validSensors.add(sensor);
			}
		}
		return validSensors;
	}

	public static void find() {
		SENSORS.clear();

		ModFileScanData scanData = ModList.get().getModFileById(SmartHome.MODID).getFile().getScanResult();
		var foundAnalyzerClasses = scanData.getAnnotatedBy(SmartHomeSensor.class, ElementType.TYPE);

		foundAnalyzerClasses.forEach(annotationData -> {
			Object modIdAnnotation = annotationData.annotationData().get("modid");
			if(!(modIdAnnotation instanceof String modid)) {
				return;
			}

			if(!ModList.get().isLoaded(modid)) {
				return;
			}

			try {
				Class<?> clazz = Class.forName(annotationData.clazz().getClassName());
				ISensor<?, ?> sensor = (ISensor<?, ?>) clazz.getDeclaredConstructor().newInstance();
				SENSORS.put(sensor.id(), sensor);

				SmartHome.LOGGER.info("Found sensor: {} for mod: {}", sensor.id(), modid);
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to instantiate sensor class: {}", annotationData.clazz().getClassName(), e);
			}
		});
	}

	public static void createTables(DuckDBConnection connection) throws SQLException {
		for(var sensor : SENSORS.values()) {
			sensor.createTable(connection);
		}
	}
}
