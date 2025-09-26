package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.api.SensorData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class EnergyStorageData extends SensorData {
	private long energyStored;
	private long maxEnergyStored;

	public EnergyStorageData(long energyStored, long maxEnergyStored) {
		this.energyStored = energyStored;
		this.maxEnergyStored = maxEnergyStored;
	}

	public long energyStored() {
		return energyStored;
	}

	public long maxEnergyStored() {
		return maxEnergyStored;
	}

	@Override
	public String displayString() {
		return energyStored + " / " + maxEnergyStored + " FE";
	}

	public static final MapCodec<EnergyStorageData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.LONG.fieldOf("energy_stored").forGetter(EnergyStorageData::energyStored),
		Codec.LONG.fieldOf("max_energy_stored").forGetter(EnergyStorageData::maxEnergyStored)
	).apply(instance, EnergyStorageData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, EnergyStorageData::energyStored,
		ByteBufCodecs.VAR_LONG, EnergyStorageData::maxEnergyStored,
		EnergyStorageData::new
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
