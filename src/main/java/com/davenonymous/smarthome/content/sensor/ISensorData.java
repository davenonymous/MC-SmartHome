package com.davenonymous.smarthome.content.sensor;

import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;

public interface ISensorData {
	default StreamCodec<RegistryFriendlyByteBuf, ? extends ISensorData> streamCodec() {
		//noinspection unchecked
		return ModSensors.DATA_STREAM_CODEC_BY_CLASS.get(this.getClass());
	}

	Object[] columnValues();

	String displayString();

	default String seriesName() {
		return "";
	}

	StreamCodec<RegistryFriendlyByteBuf, ISensorData> STREAM_CODEC = ByteBufCodecs.registry(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER_KEY)
		.dispatch(
			ISensorData::streamCodec,
			Function.identity()
	);

	int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException;
}
