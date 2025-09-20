package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class HomeZone {
	UUID id;
	AABB bounds;
	String name;

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

	public HomeZone setBounds(AABB bounds) {
		this.bounds = bounds;
		return this;
	}

	public HomeZone setName(String name) {
		this.name = name;
		return this;
	}

	public HomeZone(String name, AABB bounds) {
		this(UUID.randomUUID(), name, bounds);
	}

	public HomeZone(UUID id, String name, AABB bounds) {
		this.id = id;
		this.name = name;
		this.bounds = bounds;
	}

	public HomeCore home() {
		return home;
	}

	public HomeZone setHome(HomeCore home) {
		this.home = home;
		return this;
	}

	public static final MapCodec<HomeZone> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(HomeZone::id),
		Codec.STRING.fieldOf("name").forGetter(HomeZone::name),
		MoreCodecs.AABB_CODEC.fieldOf("bounds").forGetter(HomeZone::bounds)
	).apply(instance, HomeZone::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeZone> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, HomeZone::id,
		ByteBufCodecs.STRING_UTF8, HomeZone::name,
		MoreCodecs.AABB_STREAM_CODEC, HomeZone::bounds,
		HomeZone::new
	);

}
