package com.davenonymous.smarthome.api.sensor;

import com.davenonymous.smarthome.sensor.SensorDataCodecRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface ISensorData {
	StreamCodec<RegistryFriendlyByteBuf, ? extends ISensorData> streamCodec();

	String displayString();

	StreamCodec<RegistryFriendlyByteBuf, ISensorData> STREAM_CODEC = ByteBufCodecs.registry(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER_KEY)
		.dispatch(
			ISensorData::streamCodec,
			Function.identity()
	);
}
