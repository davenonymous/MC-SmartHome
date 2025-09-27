package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EnergyStorageData(long energyStored, long maxEnergyStored) implements ISensorData {

	@Override
	public String displayString() {
		return energyStored + " / " + maxEnergyStored + " FE";
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, EnergyStorageData::energyStored,
		ByteBufCodecs.VAR_LONG, EnergyStorageData::maxEnergyStored,
		EnergyStorageData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends ISensorData> streamCodec() {
		return STREAM_CODEC;
	}
}
