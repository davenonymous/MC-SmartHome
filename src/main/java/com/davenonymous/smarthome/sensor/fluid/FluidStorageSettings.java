package com.davenonymous.smarthome.sensor.fluid;

import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public record FluidStorageSettings(boolean enabled, Optional<Direction> face) implements SensorSettings {

	public FluidStorageSettings(boolean enabled, Direction face) {
		this(enabled, Optional.ofNullable(face));
	}

	public static final MapCodec<FluidStorageSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(FluidStorageSettings::enabled),
		Direction.CODEC.optionalFieldOf("face").forGetter(FluidStorageSettings::face)
	).apply(instance, FluidStorageSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FluidStorageSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, FluidStorageSettings::enabled,
		Direction.STREAM_CODEC.apply(ByteBufCodecs::optional), FluidStorageSettings::face,
		FluidStorageSettings::new
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
