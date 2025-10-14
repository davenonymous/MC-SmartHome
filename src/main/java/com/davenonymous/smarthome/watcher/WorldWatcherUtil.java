package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.networking.actions.devices.AddDevicePayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import org.duckdb.DuckDBConnection;

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

	public static void updateDevicesInHome(MinecraftServer server, HomeCore home) {
		var level = home.getHomeLevel(server);
		if(level == null) {
			return;
		}

		Map<HomeZone, List<FoundDevice>> result = new HashMap<>();
		for(var zone : home.zones()) {
			List<FoundDevice> foundDevices = new ArrayList<>();
			for(var pos : getBlocksInAABBStream(zone.bounds())) {
				var state = level.getBlockState(pos);
				if(zone.devices().values().stream().anyMatch(d -> d.pos().equals(pos) && d.matches(state))) {
					continue;
				}

				List<HomeSensor<?, ?>> foundSensors = ModSensors.getValidSensors(level, pos, state).stream().toList();
				if(foundSensors.isEmpty()) {
					continue;
				}

				foundDevices.add(new FoundDevice(pos, state, foundSensors.stream().map(HomeSensor::id).toList()));
			}
			result.put(zone, foundDevices);
		}

		for(var zone : home.zones()) {
			List<ConfiguredDevice> toRemove = new ArrayList<>();
			for(var device : zone.devices().values()) {
				if(!device.ignored()) {
					continue;
				}

				var state = level.getBlockState(device.pos());
				if(device.matches(state)) {
					continue;
				}

				if(!device.genericSensorsOnly()) {
					continue;
				}

				toRemove.add(device);
			}

			toRemove.forEach(zone::removeDevice);
		}

		home.setFoundDevices(result);
	}

	// Client-Side only! Only the client will now how to name a new device!
	public static void autoIgnoreGenericOnlyDevices(HomeCore home) {
		if(home != null && home.settings().autoIgnoreGenericOnlyDevices()) {
			for(var zone : home.getAllFoundDevices().keySet()) {
				List<FoundDevice> foundDevices = zone.foundDevices();
				if(foundDevices == null) {
					continue;
				}

				List<FoundDevice> filteredDevices = new ArrayList<>();
				for(var foundDevice : foundDevices) {
					var foundSensors = foundDevice.sensorIds().stream().map(ModSensors::getById);
					if(!foundSensors.allMatch(HomeSensor::isGeneric)) {
						filteredDevices.add(foundDevice);
						continue;
					}

					var deviceBlockState = foundDevice.state();
					var deviceTranslationKey = deviceBlockState.getBlock().getDescriptionId();
					//noinspection deprecation
					var deviceBlockId = deviceBlockState.getBlock().builtInRegistryHolder().getKey().location();
					ConfiguredDevice configuredDevice = new ConfiguredDevice(
						foundDevice.pos(), I18n.get(deviceTranslationKey), deviceBlockId, false, true
					);

					zone.addDevice(configuredDevice);
					PacketDistributor.sendToServer(new AddDevicePayload(home.id(), zone.id(), configuredDevice));
				}

				zone.setFoundDevices(filteredDevices);
			}
		}
	}
}
