package com.davenonymous.smarthome.content.sensor.settings;

import com.davenonymous.smarthome.content.sensor.SensorSettingsCodecRegistry;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface SensorSettings {
	default MapCodec<? extends SensorSettings> type() {
		//noinspection unchecked
		return ModSensors.SETTINGS_CODEC_BY_CLASS.get(this.getClass());
	}

	default StreamCodec<RegistryFriendlyByteBuf, ? extends SensorSettings> streamCodec() {
		//noinspection unchecked
		return ModSensors.SETTINGS_STREAM_CODEC_BY_CLASS.get(this.getClass());
	}

	boolean enabled();
	SensorSettings withEnabled(boolean enabled);

	Codec<SensorSettings> CODEC = SensorSettingsCodecRegistry.SENSOR_SETTINGS_SERIALIZERS.byNameCodec() // Gets Codec<MapCodec<? extends ExampleObject>>
		.dispatch(
			SensorSettings::type, // Get the codec from the specific object
			Function.identity() // Get the codec from the registry
    );

	StreamCodec<RegistryFriendlyByteBuf, SensorSettings> STREAM_CODEC = ByteBufCodecs.registry(SensorSettingsCodecRegistry.SENSOR_SETTINGS_DISPATCHER_KEY)
		.dispatch(
			SensorSettings::streamCodec,
			Function.identity()
	);
}
