package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.duckdb.DuckDBConnection;

import java.lang.annotation.ElementType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModSensors {
	public static List<ISensor> SENSORS = new ArrayList<>();

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
				ISensor sensor = (ISensor) clazz.getDeclaredConstructor().newInstance();
				SENSORS.add(sensor);

				SmartHome.LOGGER.info("Found sensor class: " + annotationData.clazz().getClassName() + " for mod: " + modid);
			} catch (Exception e) {
				SmartHome.LOGGER.error("Failed to instantiate sensor class: " + annotationData.clazz().getClassName(), e);
			}
		});
	}

	public static void createTables(DuckDBConnection connection) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.createTable(connection);
		}
	}
}
