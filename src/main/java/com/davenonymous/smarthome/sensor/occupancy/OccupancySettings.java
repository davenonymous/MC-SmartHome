package com.davenonymous.smarthome.sensor.occupancy;

import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record OccupancySettings(boolean enabled) implements SensorSettings {

	public static final MapCodec<OccupancySettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(OccupancySettings::enabled)
	).apply(instance, OccupancySettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, OccupancySettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, OccupancySettings::enabled,
		OccupancySettings::new
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
