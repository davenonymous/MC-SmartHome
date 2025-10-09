package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.api.sensor.sensortypes.BlockSensor;
import com.davenonymous.smarthome.api.sensor.sensortypes.EntitySensor;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.api.sensor.sensortypes.ZoneSensor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.duckdb.DuckDBConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WorldWatcher {

	private final MinecraftServer server;
	private ServerLevel overworld;
	protected long lastUpdateTick = 0;

	public WorldWatcher(MinecraftServer server) {
		this.server = server;
		this.overworld = server.overworld();
	}

	public List<Consumer<DuckDBConnection>> processHomes() {
		var homes = WorldSavedHomes.get(overworld);
		List<Consumer<DuckDBConnection>> homeConsumers = new ArrayList<>();
		for(var home : homes.homes().values()) {
			try {
				homeConsumers.addAll(processHome(home));
			} catch (SQLException e) {
				SmartHome.LOGGER.error("Error processing home for player='{}' home='{}'", home.owner(), home.name(), e);
			}
		}
		return homeConsumers;
	}

	private List<Consumer<DuckDBConnection>> processHome(HomeCore home) throws SQLException{
		var homeLevel = home.getHomeLevel(server);
		if(homeLevel == null) {
			return List.of();
		}

		List<Consumer<DuckDBConnection>> homeConsumers = new ArrayList<>();
		for(var entry : home.getAllConfiguredDevices().entrySet()) {
			var zone = entry.getKey();
			for(var device : entry.getValue()) {
				if(!device.enabled()) {
					continue;
				}

				var pos = device.pos();
				var blockState = homeLevel.getBlockState(pos);

				List<BlockSensor<?,?>> expensiveSensors = new ArrayList<>();
				var newValidSensors = ModSensors.getValidSensors(homeLevel, pos, blockState);
				for(var sensor : newValidSensors) {
					SensorSettings settings = device.sensors().get(sensor.id());
					if(settings == null) {
						settings = sensor.getDefaultSettings();
					}

					if(!settings.enabled()) {
						continue;
					}

					ISensorData data = null;
					switch(sensor) {
						case ZoneSensor<?, ?> zoneSensor -> {
							data = zoneSensor.visitZone(homeLevel, zone, device, HomeSensor.cast(settings));
						}
						case BlockSensor<?, ?> blockSensor -> {
							if(blockSensor.shouldVisitAllBlocksInZone()) {
								expensiveSensors.add(blockSensor);
							} else {
								data = blockSensor.visitZoneBlock(homeLevel, zone, device, HomeSensor.cast(settings), pos, blockState, homeLevel.getBlockEntity(pos));
							}
						}
						case EntitySensor<?, ?> entitySensor -> {
							var entities = homeLevel.getEntitiesOfClass(Entity.class, zone.bounds());
							for(var entity : entities) {
								data = entitySensor.visitZoneEntity(homeLevel, zone, device, HomeSensor.cast(settings), entity);
							}
						}
						default -> {
							SmartHome.LOGGER.warn("Sensor {} is not a ZoneSensor, BlockSensor or EntitySensor, cannot process", sensor.id());
							continue;
						}
					}

					if(data != null) {
						var handler = ModSensors.DB_HANDLERS.get(sensor.id());
						homeConsumers.add(handler.insertValues(homeLevel.getGameTime(), home.id(), zone.id(), device.id(), data));
					}
				}

				if(!expensiveSensors.isEmpty()) {
					for(var sensorCheckPos : WorldWatcherUtil.getBlocksInAABBStream(zone.bounds())) {
						var sensorCheckState = overworld.getBlockState(sensorCheckPos);
						var sensorCheckEntity = overworld.getBlockEntity(sensorCheckPos);

						for(var sensor : expensiveSensors) {
							SensorSettings settings = device.sensors().get(sensor.id());
							if(settings == null) {
								settings = sensor.getDefaultSettings();
							}

							ISensorData result = sensor.visitZoneBlock(homeLevel, zone, device, HomeSensor.cast(settings), sensorCheckPos, sensorCheckState, sensorCheckEntity);
							if(result != null) {
								var handler = ModSensors.DB_HANDLERS.get(sensor.id());
								homeConsumers.add(handler.insertValues(homeLevel.getGameTime(), home.id(), zone.id(), device.id(), result));
							}
						}
					}
				}
			}
		}

		return homeConsumers;
	}
}
