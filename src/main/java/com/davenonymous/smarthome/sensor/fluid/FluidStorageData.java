package com.davenonymous.smarthome.sensor.fluid;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record FluidStorageData(ResourceLocation fluidId, long stored, long capacity) implements ISensorData {

	@Override
	public String displayString() {
		return fluidId() + ": " + stored + " / " + capacity + " FE";
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidStorageData> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, FluidStorageData::fluidId,
		ByteBufCodecs.VAR_LONG, FluidStorageData::stored,
		ByteBufCodecs.VAR_LONG, FluidStorageData::capacity,
		FluidStorageData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends ISensorData> streamCodec() {
		return STREAM_CODEC;
	}
}
