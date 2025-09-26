package com.davenonymous.smarthome.api;

import com.davenonymous.smarthome.sensor.SensorCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public abstract class SensorSettings {
	public abstract MapCodec<? extends SensorSettings> type();
	public abstract StreamCodec<RegistryFriendlyByteBuf, ? extends SensorSettings> streamCodec();

	public static final Codec<SensorSettings> CODEC = SensorCodecRegistry.SENSOR_SETTINGS_SERIALIZERS.byNameCodec() // Gets Codec<MapCodec<? extends ExampleObject>>
		.dispatch(
			SensorSettings::type, // Get the codec from the specific object
			Function.identity() // Get the codec from the registry
    );

	public static final StreamCodec<RegistryFriendlyByteBuf, SensorSettings> STREAM_CODEC = ByteBufCodecs.registry(SensorCodecRegistry.SENSOR_SETTINGS_DISPATCHER_KEY)
		.dispatch(
			SensorSettings::streamCodec,
			Function.identity()
	);
}
