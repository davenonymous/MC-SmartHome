package com.davenonymous.smarthome.sensor.redstone;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RedstonePoweredData(int redstoneLevel) implements ISensorData {
	@Override
	public String displayString() {
		return "" + redstoneLevel;
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, RedstonePoweredData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, RedstonePoweredData::redstoneLevel,
		RedstonePoweredData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends ISensorData> streamCodec() {
		return STREAM_CODEC;
	}
}
