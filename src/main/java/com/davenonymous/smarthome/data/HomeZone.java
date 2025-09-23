package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.setup.WorldWatcher;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
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
	List<IgnoredDevice> ignoredDevices;

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

	public List<IgnoredDevice> ignoredDevices() {
		return ignoredDevices;
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

	public HomeZone(UUID id, String name, AABB bounds, List<ConfiguredDevice> devices, List<IgnoredDevice> ignoredDevices, boolean deleted) {
		this.id = id;
		this.name = name;
		this.bounds = bounds;
		this.deleted = deleted;
		this.devices = new ArrayList<>(devices);
		this.ignoredDevices = new ArrayList<>(ignoredDevices);
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

	public static final MapCodec<HomeZone> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(HomeZone::id),
		Codec.STRING.fieldOf("name").forGetter(HomeZone::name),
		MoreCodecs.AABB_CODEC.fieldOf("bounds").forGetter(HomeZone::bounds),
		ConfiguredDevice.CODEC.codec().listOf().optionalFieldOf("devices", List.of()).forGetter(HomeZone::devices),
		IgnoredDevice.CODEC.codec().listOf().optionalFieldOf("ignoredDevices", List.of()).forGetter(HomeZone::ignoredDevices),
		Codec.BOOL.optionalFieldOf("deleted", false).forGetter(HomeZone::isDeleted)
	).apply(instance, HomeZone::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeZone> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, HomeZone::id,
		ByteBufCodecs.STRING_UTF8, HomeZone::name,
		MoreCodecs.AABB_STREAM_CODEC, HomeZone::bounds,
		ConfiguredDevice.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeZone::devices,
		IgnoredDevice.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeZone::ignoredDevices,
		ByteBufCodecs.BOOL, HomeZone::isDeleted,
		HomeZone::new
	);

}
