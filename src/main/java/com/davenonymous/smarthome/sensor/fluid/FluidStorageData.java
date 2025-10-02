package com.davenonymous.smarthome.sensor.fluid;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record FluidStorageData(String fluidId, long stored, long capacity) implements ISensorData {

	@Override
	public Object[] columnValues() {
		return new Object[] {fluidId, stored, capacity};
	}

	@Override
	public String displayString() {
		return fluidId() + ": " + stored + " / " + capacity + " FE";
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setString(nextParamIndex++, fluidId());
		prepped.setLong(nextParamIndex++, stored());
		prepped.setLong(nextParamIndex++, capacity());
		return nextParamIndex;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, FluidStorageData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, FluidStorageData::fluidId,
		ByteBufCodecs.VAR_LONG, FluidStorageData::stored,
		ByteBufCodecs.VAR_LONG, FluidStorageData::capacity,
		FluidStorageData::new
	);
}
