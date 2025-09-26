package com.davenonymous.smarthome.api;

import com.davenonymous.smarthome.sensor.SensorDataCodecRegistry;
import com.davenonymous.smarthome.sensor.SensorSettingsCodecRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public abstract class SensorData {
	public abstract MapCodec<? extends SensorData> type();
	public abstract StreamCodec<RegistryFriendlyByteBuf, ? extends SensorData> streamCodec();

	public abstract String displayString();

	public static final Codec<SensorData> CODEC = SensorDataCodecRegistry.SENSOR_DATA_SERIALIZERS.byNameCodec() // Gets Codec<MapCodec<? extends ExampleObject>>
		.dispatch(
			SensorData::type, // Get the codec from the specific object
			Function.identity() // Get the codec from the registry
    );

	public static final StreamCodec<RegistryFriendlyByteBuf, SensorData> STREAM_CODEC = ByteBufCodecs.registry(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER_KEY)
		.dispatch(
			SensorData::streamCodec,
			Function.identity()
	);
}
