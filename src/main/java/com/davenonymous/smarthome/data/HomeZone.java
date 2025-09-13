package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.AABB;

public class HomeZone {
	AABB bounds;
	String name;

	HomeCore home;

	public String name() {
		return name;
	}

	public AABB bounds() {
		return bounds;
	}

	public HomeZone(String name, AABB bounds) {
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

	public static final MapCodec<AABB> AABB_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("minX").forGetter(aabb -> aabb.minX),
		Codec.DOUBLE.fieldOf("minY").forGetter(aabb -> aabb.minY),
		Codec.DOUBLE.fieldOf("minZ").forGetter(aabb -> aabb.minZ),
		Codec.DOUBLE.fieldOf("maxX").forGetter(aabb -> aabb.maxX),
		Codec.DOUBLE.fieldOf("maxY").forGetter(aabb -> aabb.maxY),
		Codec.DOUBLE.fieldOf("maxZ").forGetter(aabb -> aabb.maxZ)
	).apply(instance, AABB::new));

	public static final MapCodec<HomeZone> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(HomeZone::name),
		AABB_CODEC.fieldOf("bounds").forGetter(HomeZone::bounds)
	).apply(instance, HomeZone::new));
}
