package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.AABB;

public class HomeZone {
	AABB shape;
	String name;

	public String name() {
		return name;
	}

	public AABB shape() {
		return shape;
	}

	public HomeZone(String name, AABB shape) {
		this.name = name;
		this.shape = shape;
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
		AABB_CODEC.fieldOf("shape").forGetter(HomeZone::shape)
	).apply(instance, HomeZone::new));
}
