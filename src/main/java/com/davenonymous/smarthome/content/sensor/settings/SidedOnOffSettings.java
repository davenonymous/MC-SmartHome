package com.davenonymous.smarthome.content.sensor.settings;

import com.davenonymous.smarthome.content.sensor.annotation.HomeSensorSettings;
import com.davenonymous.smarthome.content.sensor.annotation.SensorSettingsCodec;
import com.davenonymous.smarthome.content.sensor.annotation.SensorSettingsDefault;
import com.davenonymous.smarthome.content.sensor.annotation.SensorSettingsStreamCodec;
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
