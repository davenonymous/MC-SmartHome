package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SmartHomeSensor;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
			sensor.createTables(connection);
		}
	}

	public static void callVisitHome(DuckDBConnection connection, MinecraftServer server, HomeCore home) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.visitHome(connection, server, home);
		}
	}

	public static void callVisitHomeEntity(DuckDBConnection connection, MinecraftServer server, HomeCore home, Entity entity) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.visitHomeEntity(connection, server, home, entity);
		}
	}

	public static void callVisitHomeBlock(DuckDBConnection connection, MinecraftServer server, HomeCore home, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.visitHomeBlock(connection, server, home, pos, state, blockEntity);
		}
	}

	public static void callVisitZone(DuckDBConnection connection, MinecraftServer server, HomeZone zone) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.visitZone(connection, server, zone);
		}
	}

	public static void callVisitZoneEntity(DuckDBConnection connection, MinecraftServer server, HomeZone zone, Entity entity) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.visitZoneEntity(connection, server, zone, entity);
		}
	}

	public static void callVisitZoneBlock(DuckDBConnection connection, MinecraftServer server, HomeZone zone, BlockPos pos, BlockState state, BlockEntity blockEntity) throws SQLException {
		for(var sensor : SENSORS) {
			sensor.visitZoneBlock(connection, server, zone, pos, state, blockEntity);
		}
	}

}
