package com.davenonymous.smarthome.sensor.redstone;

import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RedstonePoweredSettings(boolean enabled) implements SensorSettings {
	public static final MapCodec<RedstonePoweredSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(RedstonePoweredSettings::enabled)
	).apply(instance, RedstonePoweredSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RedstonePoweredSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, RedstonePoweredSettings::enabled,
		RedstonePoweredSettings::new
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
