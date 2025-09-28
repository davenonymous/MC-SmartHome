package com.davenonymous.smarthome.sensor.energy;

import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.sensor.occupancy.OccupancySettings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record EnergyStorageSettings(boolean enabled, Optional<Direction> face) implements SensorSettings {

	public EnergyStorageSettings(boolean enabled, Direction face) {
		this(enabled, Optional.ofNullable(face));
	}

	public static final MapCodec<EnergyStorageSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(EnergyStorageSettings::enabled),
		Direction.CODEC.optionalFieldOf("face").forGetter(EnergyStorageSettings::face)
	).apply(instance, EnergyStorageSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, EnergyStorageSettings::enabled,
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
