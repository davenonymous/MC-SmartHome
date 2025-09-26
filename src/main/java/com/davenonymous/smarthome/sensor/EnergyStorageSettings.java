package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.api.SensorSettings;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class EnergyStorageSettings extends SensorSettings {
	Direction face;

	public EnergyStorageSettings() {
		this.face = null;
	}

	public EnergyStorageSettings(Optional<Direction> face) {
		this.face = face.orElse(null);
	}

	public Optional<Direction> face() {
		return Optional.ofNullable(face);
	}

	public static final MapCodec<EnergyStorageSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Direction.CODEC.optionalFieldOf("face").forGetter(EnergyStorageSettings::face)
	).apply(instance, EnergyStorageSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageSettings> STREAM_CODEC = StreamCodec.composite(
		Direction.STREAM_CODEC.apply(ByteBufCodecs::optional), EnergyStorageSettings::face,
		EnergyStorageSettings::new
	);

	@Override
	public MapCodec<? extends SensorSettings> type() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends SensorSettings> streamCodec() {
		return STREAM_CODEC;
	}
}
