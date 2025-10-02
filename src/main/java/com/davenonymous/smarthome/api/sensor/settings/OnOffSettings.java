package com.davenonymous.smarthome.api.sensor.settings;

import com.davenonymous.smarthome.api.sensor.annotations.HomeSensorSettings;
import com.davenonymous.smarthome.api.sensor.annotations.SensorSettingsCodec;
import com.davenonymous.smarthome.api.sensor.annotations.SensorSettingsDefault;
import com.davenonymous.smarthome.api.sensor.annotations.SensorSettingsStreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;


@HomeSensorSettings
public record OnOffSettings(boolean enabled) implements SensorSettings {

	@SensorSettingsCodec
	public static final MapCodec<OnOffSettings> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(OnOffSettings::enabled)
	).apply(instance, OnOffSettings::new));

	@SensorSettingsStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, OnOffSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, OnOffSettings::enabled,
		OnOffSettings::new
	);

	@SensorSettingsDefault
	public static final OnOffSettings DEFAULT = new OnOffSettings(false);

	@Override
	public SensorSettings withEnabled(boolean enabled) {
		return new OnOffSettings(enabled);
	}
}
