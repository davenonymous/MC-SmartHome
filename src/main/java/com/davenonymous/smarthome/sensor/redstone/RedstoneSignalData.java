package com.davenonymous.smarthome.sensor.redstone;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record RedstoneSignalData(int outputLevel, int maxNeighborLevel) implements ISensorData {

	@Override
	public Object[] columnValues() {
		return new Object[] {outputLevel, maxNeighborLevel};
	}

	@Override
	public String displayString() {
		return "" + outputLevel;
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setInt(nextParamIndex++, outputLevel());
		prepped.setInt(nextParamIndex++, maxNeighborLevel());
		return nextParamIndex;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, RedstoneSignalData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, RedstoneSignalData::outputLevel,
		ByteBufCodecs.INT, RedstoneSignalData::maxNeighborLevel,
		RedstoneSignalData::new
	);
}
