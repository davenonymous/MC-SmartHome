package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class WorldWatcher {

	private final MinecraftServer server;
	private ServerLevel overworld;
	private long lastTick = 0;


	public WorldWatcher(MinecraftServer server) {
		this.server = server;
		this.overworld = server.overworld();
	}

	public List<ThrowingConsumer<DuckDBConnection>> processHomes() {
		var homes = WorldSavedHomes.get(overworld);
		List<ThrowingConsumer<DuckDBConnection>> homeConsumers = new ArrayList<>();
		for(var home : homes.homes().values()) {
			try {
				homeConsumers.addAll(processHome(home));
			} catch (SQLException e) {
				SmartHome.LOGGER.error("Error processing home for player='{}' home='{}'", home.owner(), home.name(), e);
			}
		}
		return homeConsumers;
	}

	private List<ThrowingConsumer<DuckDBConnection>> processHome(HomeCore home) throws SQLException{
		var homeLevel = home.getHomeLevel(server);
		if(homeLevel == null) {
			return List.of();
		}

		List<ThrowingConsumer<DuckDBConnection>> homeConsumers = new ArrayList<>();
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
					homeConsumers.add(sensor.visitZone(homeLevel, zone, device));
					homeConsumers.add(sensor.visitZoneBlock(homeLevel, zone, device, pos, blockState, homeLevel.getBlockEntity(pos)));

					var entities = homeLevel.getEntitiesOfClass(Entity.class, zone.bounds());
					for(var entity : entities) {
						homeConsumers.add(sensor.visitZoneEntity(homeLevel, zone, device, entity));
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
							homeConsumers.add(sensor.visitZoneBlock(homeLevel, zone, device, sensorCheckPos, sensorCheckState, sensorCheckEntity));
						}
					}
				}
			}
		}

		return homeConsumers;
	}
}
