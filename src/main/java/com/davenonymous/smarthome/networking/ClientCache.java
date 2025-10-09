package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;

public class ClientCache {
	public static final ClientCache INSTANCE = new ClientCache();

	public Map<UUID, Pair<HomeCore, HomeWorldInfo>> homeCache = new HashMap<>();
	public Map<UUID, Map<ResourceLocation, ISensorData>> sensorDataCache = new HashMap<>();
	//          (DevID, SensorId) ->     {VizID ->              [ (Timestamp, Tick) -> Data ]}
	public Table<UUID, ResourceLocation, Map<ResourceLocation, LinkedHashMap<Pair<Instant, Long>, ISensorData>>> visualizationDataCache = TreeBasedTable.create();


	public static void addHomeInfo(HomeCore home, HomeWorldInfo worldInfo) {
		INSTANCE.homeCache.put(home.id(), Pair.of(home, worldInfo));
	}

	public static Optional<Pair<HomeCore, HomeWorldInfo>> getHome(UUID homeId) {
		if(INSTANCE.homeCache.containsKey(homeId)) {
			return Optional.of(INSTANCE.homeCache.get(homeId));
		}

		return Optional.empty();
	}

	public static @NotNull Map<UUID, LinkedHashMap<Pair<Instant, Long>, ISensorData>> dataByDevices(List<UUID> deviceIds, ResourceLocation sensorId, ResourceLocation vizId) {
		Map<UUID, LinkedHashMap<Pair<Instant, Long>, ISensorData>> result = new HashMap<>();
		for(var deviceId : deviceIds) {
			var vizMap = getVizMap(deviceId, sensorId);
			if(vizMap.containsKey(vizId)) {
				result.put(deviceId, vizMap.get(vizId));
			}
		}
		return result;
	}

	public static @Nullable ISensorData getSensorData(UUID deviceId, ResourceLocation sensorId) {
		if(!INSTANCE.sensorDataCache.containsKey(deviceId)) {
			return null;
		}
		return INSTANCE.sensorDataCache.get(deviceId).get(sensorId);
	}

	public static @NotNull Map<ResourceLocation, LinkedHashMap<Pair<Instant, Long>, ISensorData>> getVizMap(UUID deviceId, ResourceLocation sensorId) {
		if(!INSTANCE.visualizationDataCache.contains(deviceId, sensorId)) {
			return Collections.emptyMap();
		}
		//noinspection DataFlowIssue
		return INSTANCE.visualizationDataCache.get(deviceId, sensorId);
	}

	public static @NotNull Map<ResourceLocation, ISensorData> getDeviceData(UUID deviceId) {
		if(!INSTANCE.sensorDataCache.containsKey(deviceId)) {
			return Collections.emptyMap();
		}
		return INSTANCE.sensorDataCache.get(deviceId);
	}

	public static Optional<Pair<HomeZone, ConfiguredDevice>> getConfiguredDevice(UUID deviceId) {
		for(var homePair : INSTANCE.homeCache.values()) {
			HomeCore home = homePair.getFirst();
			var optDevice = home.getDevice(deviceId);
			if(optDevice.isPresent()) {
				return optDevice;
			}
		}
		return Optional.empty();
	}

	public static void setSensorData(UUID deviceId, Map<ResourceLocation, ISensorData> data) {
		INSTANCE.sensorDataCache.put(deviceId, new HashMap<>(data));
	}

	public static void setVisualizationData(UUID deviceId, ResourceLocation sensorId, ResourceLocation vizId, LinkedHashMap<Pair<Instant, Long>, ISensorData> data) {
		if(!INSTANCE.visualizationDataCache.contains(deviceId, sensorId)) {
			INSTANCE.visualizationDataCache.put(deviceId, sensorId, new HashMap<>());
		}
		INSTANCE.visualizationDataCache.get(deviceId, sensorId).put(vizId, data);

	}
}
