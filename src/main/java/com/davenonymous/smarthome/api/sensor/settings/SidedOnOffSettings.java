package com.davenonymous.smarthome.api.sensor.settings;

import com.davenonymous.smarthome.api.sensor.annotations.HomeSensorSettings;
import com.davenonymous.smarthome.api.sensor.annotations.SensorSettingsCodec;
import com.davenonymous.smarthome.api.sensor.annotations.SensorSettingsDefault;
import com.davenonymous.smarthome.api.sensor.annotations.SensorSettingsStreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;


@HomeSensorSettings
public record SidedOnOffSettings(boolean enabled, Optional<Direction> side) implements SensorSettings {

	@SensorSettingsCodec
	public static final MapCodec<SidedOnOffSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(SidedOnOffSettings::enabled),
		Direction.CODEC.optionalFieldOf("side").forGetter(SidedOnOffSettings::side)
	).apply(instance, SidedOnOffSettings::new));

	@SensorSettingsStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SidedOnOffSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, SidedOnOffSettings::enabled,
		Direction.STREAM_CODEC.apply(ByteBufCodecs::optional), SidedOnOffSettings::side,
		SidedOnOffSettings::new
	);

	@SensorSettingsDefault
	public static final SidedOnOffSettings DEFAULT = new SidedOnOffSettings(false, Optional.empty());

	@Override
	public SensorSettings withEnabled(boolean enabled) {
		return new SidedOnOffSettings(enabled, this.side);
	}
}
