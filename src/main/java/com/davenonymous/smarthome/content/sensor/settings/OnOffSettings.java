package com.davenonymous.smarthome.content.sensor.settings;

import com.davenonymous.smarthome.content.sensor.annotation.HomeSensorSettings;
import com.davenonymous.smarthome.content.sensor.annotation.SensorSettingsCodec;
import com.davenonymous.smarthome.content.sensor.annotation.SensorSettingsDefault;
import com.davenonymous.smarthome.content.sensor.annotation.SensorSettingsStreamCodec;
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
