package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.api.SensorData;
import com.davenonymous.smarthome.api.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.setup.content.ModSensors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.AABB;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldWatcherUtil {

	public static <T extends SensorSettings, U extends SensorData> CompletableFuture<U> getSensorState(HomeZone zone, ConfiguredDevice device, T settings) {
		//noinspection unchecked
		ISensor<T, U> sensor = (ISensor<T, U>) ModSensors.getBySettings(settings);
		if(sensor == null) {
			return CompletableFuture.completedFuture(null);
		}
		return getSensorState(zone, device, sensor);
	}

	private static <U extends SensorData> CompletableFuture<U> getSensorState(HomeZone zone, ConfiguredDevice device, ISensor<?, U> sensor) {
		return WorldWatcherPool
			.query(sensor.stateForDevice(zone, device))
			.thenApply(resultSet -> {
				try {
					if(resultSet.next()) {
						return sensor.getStateFromResultSet(resultSet);
					}
					return null;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			});
	}

	public static <T extends SensorSettings, U extends SensorData> CompletableFuture<Map<Long, U>> getSensorHistory(HomeZone zone, ConfiguredDevice device, T settings, long start, long end) {
		//noinspection unchecked
		ISensor<T, U> sensor = (ISensor<T, U>) ModSensors.getBySettings(settings);
		if(sensor == null) {
			return CompletableFuture.completedFuture(null);
		}

		return getSensorHistory(zone, device, sensor, start, end);
	}

	private static <U extends SensorData> CompletableFuture<Map<Long, U>> getSensorHistory(HomeZone zone, ConfiguredDevice device, ISensor<?, U> sensor, long start, long end) {
		return WorldWatcherPool
			.query(sensor.historyForDevice(zone, device, start, end))
			.thenApply(sensor::getHistoryFromResultSet);
	}

	public static List<BlockPos> getBlocksInAABBStream(AABB box) {
		int minX = (int)Math.floor(box.minX);
		int minY = (int)Math.floor(box.minY);
		int minZ = (int)Math.floor(box.minZ);
		int maxX = (int)Math.ceil(box.maxX)-1;
		int maxY = (int)Math.ceil(box.maxY)-1;
		int maxZ = (int)Math.ceil(box.maxZ)-1;

		var positions = new LinkedList<BlockPos>();
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
				for(int z = minZ; z <= maxZ; z++) {
					positions.add(new BlockPos(x, y, z));
				}
			}
		}
		return positions;
	}

	public static Map<HomeZone, List<FoundDevice>> searchForDevices(MinecraftServer server, HomeCore home) {
		var level = home.getHomeLevel(server);
		if(level == null) {
			return Map.of();
		}

		Map<HomeZone, List<FoundDevice>> result = new HashMap<>();
		for(var zone : home.zones()) {
			List<FoundDevice> foundDevices = new ArrayList<>();
			for(var pos : getBlocksInAABBStream(zone.bounds())) {
				var state = level.getBlockState(pos);
				if(zone.devices().stream().anyMatch(d -> d.pos().equals(pos) && d.matches(state))) {
					continue;
				}

				List<ResourceLocation> foundSensors = new ArrayList<>();
				for(var sensor : ModSensors.SENSORS) {
					if(!sensor.isValid(level, pos, state)) {
						continue;
					}

					foundSensors.add(sensor.id());
				}
				if(foundSensors.isEmpty()) {
					continue;
				}
				foundDevices.add(new FoundDevice(pos, state, foundSensors));
			}
			result.put(zone, foundDevices);
		}

		return result;
	}
}
