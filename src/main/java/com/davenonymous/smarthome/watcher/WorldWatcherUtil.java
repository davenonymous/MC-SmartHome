package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.setup.content.ModSensors;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.AABB;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WorldWatcherUtil {

	public static CompletableFuture<ResultSet> getSensorState(HomeZone zone, ConfiguredDevice device, ISensor sensor) {
		return WorldWatcherPool.query(sensor.stateForDevice(zone, device));
	}

	public static CompletableFuture<ResultSet> getSensorHistory(HomeZone zone, ConfiguredDevice device, ISensor sensor, long start, long end) {
		return WorldWatcherPool.query(sensor.historyForDevice(zone, device, start, end));
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
