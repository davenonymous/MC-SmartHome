package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.api.SensorData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class RedstonePoweredData extends SensorData {
	private int redstoneLevel;

	public RedstonePoweredData(int redstoneLevel) {
		this.redstoneLevel = redstoneLevel;
	}

	@Override
	public String displayString() {
		return "" + redstoneLevel;
	}

	public int redstoneLevel() {
		return redstoneLevel;
	}

	public static final MapCodec<RedstonePoweredData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.INT.fieldOf("level").forGetter(RedstonePoweredData::redstoneLevel)
	).apply(instance, RedstonePoweredData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RedstonePoweredData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, RedstonePoweredData::redstoneLevel,
		RedstonePoweredData::new
	);

	@Override
	public MapCodec<? extends SensorData> type() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends SensorData> streamCodec() {
		return STREAM_CODEC;
	}
}
