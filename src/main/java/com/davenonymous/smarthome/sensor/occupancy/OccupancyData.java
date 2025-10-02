package com.davenonymous.smarthome.sensor.occupancy;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record OccupancyData(int id, String name, String type, String category, double x, double y, double z) implements ISensorData {

	@Override
	public Object[] columnValues() {
		return new Object[] {id, name, type, category, x, y, z};
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, OccupancyData> STREAM_CODEC = BiggerStreamCodec.composite(
		ByteBufCodecs.INT, OccupancyData::id,
		ByteBufCodecs.STRING_UTF8, OccupancyData::name,
		ByteBufCodecs.STRING_UTF8, OccupancyData::type,
		ByteBufCodecs.STRING_UTF8, OccupancyData::category,
		ByteBufCodecs.DOUBLE, OccupancyData::x,
		ByteBufCodecs.DOUBLE, OccupancyData::y,
		ByteBufCodecs.DOUBLE, OccupancyData::z,
		OccupancyData::new
	);

	@Override
	public String displayString() {
		return name;
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setInt(nextParamIndex++, id());
		prepped.setString(nextParamIndex++, name());
		prepped.setString(nextParamIndex++, type());
		prepped.setString(nextParamIndex++, category());
		prepped.setDouble(nextParamIndex++, x());
		prepped.setDouble(nextParamIndex++, y());
		prepped.setDouble(nextParamIndex++, z());
		return nextParamIndex;
	}
}
