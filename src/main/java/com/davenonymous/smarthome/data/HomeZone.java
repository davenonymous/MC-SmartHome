package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeZone {
	UUID id;
	AABB bounds;
	String name;
	boolean deleted;

	List<ConfiguredDevice> devices;
	List<FoundDevice> foundDevices;

	HomeCore home;

	public String name() {
		return name;
	}

	public AABB bounds() {
		return bounds;
	}

	public UUID id() {
		return id;
	}

	public List<ConfiguredDevice> devices() {
		return devices;
	}

	public HomeZone setFoundDevices(List<FoundDevice> foundDevices) {
		this.foundDevices = new ArrayList<>(foundDevices);
		return this;
	}

	public List<FoundDevice> foundDevices() {
		return foundDevices;
	}

	public HomeZone setBounds(AABB bounds) {
		this.bounds = bounds;
		return this;
	}

	public HomeZone setName(String name) {
		this.name = name;
		return this;
	}

	public HomeZone(String name, AABB bounds) {
		this(UUID.randomUUID(), name, bounds, List.of(), List.of(), false);
	}

	public HomeZone(UUID id, String name, AABB bounds, List<ConfiguredDevice> devices, List<FoundDevice> foundDevices, boolean deleted) {
		this.id = id;
		this.name = name;
		this.bounds = bounds;
		this.deleted = deleted;
		this.devices = new ArrayList<>(devices);
		this.foundDevices = new ArrayList<>(foundDevices);
	}

	public HomeCore home() {
		return home;
	}

	public HomeZone setHome(HomeCore home) {
		this.home = home;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public HomeZone setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public void addDevice(ConfiguredDevice device) {
		this.devices.add(device);
	}

	public void removeDevice(ConfiguredDevice device) {
		this.devices.removeIf(d -> d.id().equals(device.id()));
	}

	public void setDeviceName(ConfiguredDevice device, String newName) {
		for(int i = 0; i < devices.size(); i++) {
			var d = devices.get(i);
			if(d.id().equals(device.id())) {
				devices.set(i, devices.get(i).withName(newName));
				return;
			}
		}
	}

	public void setDeviceState(ConfiguredDevice device, boolean enabled) {
		for(int i = 0; i < devices.size(); i++) {
			var d = devices.get(i);
			if(d.id().equals(device.id())) {
				devices.set(i, devices.get(i).withEnabled(enabled));
				return;
			}
		}
	}

	public void setSensorState(ConfiguredDevice device, ResourceLocation sensorId, boolean enabled) {
		for(int i = 0; i < devices.size(); i++) {
			var d = devices.get(i);
			if(d.id().equals(device.id())) {
				var dev = devices.get(i);
				var existingSensorConfig = dev.sensors();
				if(!existingSensorConfig.containsKey(sensorId)) {
					return;
				}

				var newSensorSettings = existingSensorConfig.get(sensorId).withEnabled(enabled);
				existingSensorConfig.put(sensorId, newSensorSettings);
				devices.set(i, dev.withSensors(existingSensorConfig));
				return;
			}
		}
	}

	public static final MapCodec<HomeZone> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(HomeZone::id),
		Codec.STRING.fieldOf("name").forGetter(HomeZone::name),
		MoreCodecs.AABB_CODEC.fieldOf("bounds").forGetter(HomeZone::bounds),
		ConfiguredDevice.CODEC.codec().listOf().optionalFieldOf("devices", List.of()).forGetter(HomeZone::devices),
		FoundDevice.CODEC.codec().listOf().optionalFieldOf("foundDevices", List.of()).forGetter(HomeZone::foundDevices),
		Codec.BOOL.optionalFieldOf("deleted", false).forGetter(HomeZone::isDeleted)
	).apply(instance, HomeZone::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeZone> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, HomeZone::id,
		ByteBufCodecs.STRING_UTF8, HomeZone::name,
		MoreCodecs.AABB_STREAM_CODEC, HomeZone::bounds,
		ConfiguredDevice.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeZone::devices,
		FoundDevice.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeZone::foundDevices,
		ByteBufCodecs.BOOL, HomeZone::isDeleted,
		HomeZone::new
	);

	public HomeZone updateDevices(List<ConfiguredDevice> newDevices) {
		this.devices = new ArrayList<>(newDevices);
		return this;
	}
}
