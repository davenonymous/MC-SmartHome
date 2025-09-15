package com.davenonymous.smarthome.util;

import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MoreCodecs {


	public static final Codec<Axis> AXIS_CODEC = Codec.BYTE.xmap(AxisDirectionHelper::axisFromByte, AxisDirectionHelper::axisToByte);
	public static final StreamCodec<FriendlyByteBuf, Axis> AXIS_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BYTE, AxisDirectionHelper::axisToByte,
		AxisDirectionHelper::axisFromByte
	);
	public static final StreamCodec<FriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, Vec3::x,
		ByteBufCodecs.DOUBLE, Vec3::y,
		ByteBufCodecs.DOUBLE, Vec3::z,
		Vec3::new
	);
	public static final MapCodec<AABB> AABB_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.DOUBLE.fieldOf("minX").forGetter(aabb -> aabb.minX),
		Codec.DOUBLE.fieldOf("minY").forGetter(aabb -> aabb.minY),
		Codec.DOUBLE.fieldOf("minZ").forGetter(aabb -> aabb.minZ),
		Codec.DOUBLE.fieldOf("maxX").forGetter(aabb -> aabb.maxX),
		Codec.DOUBLE.fieldOf("maxY").forGetter(aabb -> aabb.maxY),
		Codec.DOUBLE.fieldOf("maxZ").forGetter(aabb -> aabb.maxZ)
	).apply(instance, AABB::new));
	public static final StreamCodec<FriendlyByteBuf, AABB> AABB_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, aabb -> aabb.minX,
		ByteBufCodecs.DOUBLE, aabb -> aabb.minY,
		ByteBufCodecs.DOUBLE, aabb -> aabb.minZ,
		ByteBufCodecs.DOUBLE, aabb -> aabb.maxX,
		ByteBufCodecs.DOUBLE, aabb -> aabb.maxY,
		ByteBufCodecs.DOUBLE, aabb -> aabb.maxZ,
		AABB::new
	);
}
