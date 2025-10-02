package com.davenonymous.smarthome.sensor.redstone;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record RedstonePoweredData(int redstoneLevel) implements ISensorData {
	@Override
	public String displayString() {
		return "" + redstoneLevel;
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setInt(nextParamIndex++, redstoneLevel());
		return nextParamIndex;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, RedstonePoweredData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.INT, RedstonePoweredData::redstoneLevel,
		RedstonePoweredData::new
	);
}
