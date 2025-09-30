package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.AABB;
import org.duckdb.DuckDBConnection;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WorldWatcherUtil {

	public static <U extends ISensorData> CompletableFuture<U> getSensorState(ConfiguredDevice device, ResourceLocation sensorId) {
		//noinspection unchecked
		HomeSensor<U, ?> sensor = (HomeSensor<U, ?>) ModSensors.getById(sensorId);
		if(sensor == null) {
			return CompletableFuture.completedFuture(null);
		}
		return getSensorState(device, sensor);
	}

	private static <U extends ISensorData> CompletableFuture<U> getSensorState(ConfiguredDevice device, HomeSensor<U, ?> sensor) {
		//noinspection unchecked
		return (CompletableFuture<U>) WorldWatcherPool
			.query((Function<DuckDBConnection, ISensorData>) sensor.getDBHandler().getLatestValue(device.id()));
	}

	public static <U extends ISensorData> CompletableFuture<LinkedHashMap<Pair<Instant, Long>, ?>> getSensorHistory(ConfiguredDevice device, ResourceLocation sensorId, long start, long end) {
		//noinspection unchecked
		HomeSensor<U, ?> sensor = (HomeSensor<U, ?>) ModSensors.getById(sensorId);
		if(sensor == null) {
			return CompletableFuture.completedFuture(null);
		}

		return getSensorHistory(device, sensor, start, end);
	}

	private static <U extends ISensorData> CompletableFuture<LinkedHashMap<Pair<Instant, Long>, ?>> getSensorHistory(ConfiguredDevice device, HomeSensor<U, ?> sensor, long start, long end) {
		return WorldWatcherPool
			.fullQuery(sensor.getDBHandler().getValues(device.id(), start, end));
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

				List<ResourceLocation> foundSensors = ModSensors.getValidSensors(level, pos, state).stream().map(HomeSensor::id).toList();
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
