package com.davenonymous.smarthome.util;

import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
}
