package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class WorldWatcher implements Runnable {
	public static DuckDBConnection connection;

	private MinecraftServer server;
	private ServerLevel overworld;
	private long lastTick = 0;
	public static final Logger LOGGER = LogUtils.getLogger();

	public WorldWatcher(MinecraftServer server) {
		this.server = server;
		this.overworld = server.overworld();
	}


	private void processHome(HomeCore home) throws SQLException{
		var homeLevel = home.getHomeLevel(server);
		if(homeLevel == null) {
			return;
		}

		for(var entry : home.getAllConfiguredDevices().entrySet()) {
			var zone = entry.getKey();
			for(var device : entry.getValue()) {
				if(!device.enabled()) {
					continue;
				}

				var pos = device.pos();
				var blockState = homeLevel.getBlockState(pos);
				var validSensors = ModSensors.SENSORS.stream().filter(sensor -> sensor.isValid(homeLevel, pos, blockState)).toList();
				if(validSensors.isEmpty()) {
					continue;
				}

				List<ISensor> expensiveSensors = new ArrayList<>();
				for(var sensor : validSensors) {
					sensor.visitZone(connection, homeLevel, zone, device);
					sensor.visitZoneBlock(connection, homeLevel, zone, device, pos, blockState, homeLevel.getBlockEntity(pos));
					var entities = overworld.getEntitiesOfClass(Entity.class, zone.bounds());
					for(var entity : entities) {
						sensor.visitZoneEntity(connection, homeLevel, zone, device, entity);
					}
					if(sensor.shouldVisitAllBlocksInZone()) {
						expensiveSensors.add(sensor);
					}
				}

				if(!expensiveSensors.isEmpty()) {
					for(var sensorCheckPos : WorldWatcherUtil.getBlocksInAABBStream(zone.bounds())) {
						var sensorCheckState = overworld.getBlockState(sensorCheckPos);
						var sensorCheckEntity = overworld.getBlockEntity(sensorCheckPos);

						for(var sensor : expensiveSensors) {
							sensor.visitZoneBlock(connection, homeLevel, zone, device, sensorCheckPos, sensorCheckState, sensorCheckEntity);
						}
					}
				}
			}
		}
	}

	@Override
	public void run() {
		LOGGER.info("Establishing DuckDB connection");
		try {
			connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:" + server.getWorldPath(LevelResource.ROOT).resolve("smarthome.duckdb"));
			ModSensors.createTables(connection);
		} catch (SQLException e) {
			SmartHome.LOGGER.error("Error initializing DuckDB", e);
			throw new RuntimeException(e);
		}

		LOGGER.info("Entering world watcher loop");
		while(true) {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				break;
			}

			if(lastTick == server.getTickCount()) {
				continue;
			}

			lastTick = server.getTickCount();

			var homes = WorldSavedHomes.get(overworld);
			for(var home : homes.homes().values()) {
				try {
					processHome(home);
				} catch (SQLException e) {
					SmartHome.LOGGER.error("Error processing home for player='{}' home='{}'", home.owner(), home.name(), e);
				}
			}

		}

		LOGGER.info("Exiting world watcher loop, closing DuckDB connection");
		try {
			connection.close();
		} catch (SQLException e) {
			SmartHome.LOGGER.error("Error closing DuckDB connection", e);
		}

		LOGGER.info("Stopping world watcher thread");
	}
}
