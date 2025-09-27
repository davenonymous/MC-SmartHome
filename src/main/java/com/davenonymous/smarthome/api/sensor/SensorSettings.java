package com.davenonymous.smarthome.api.sensor;

import com.davenonymous.smarthome.sensor.SensorSettingsCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface SensorSettings {
	MapCodec<? extends SensorSettings> type();
	StreamCodec<RegistryFriendlyByteBuf, ? extends SensorSettings> streamCodec();

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
