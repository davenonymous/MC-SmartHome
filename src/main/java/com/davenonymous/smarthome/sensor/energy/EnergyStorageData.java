package com.davenonymous.smarthome.sensor.energy;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record EnergyStorageData(long energyStored, long maxEnergyStored) implements ISensorData {

	@Override
	public String displayString() {
		return energyStored + " / " + maxEnergyStored + " FE";
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setLong(nextParamIndex++, energyStored());
		prepped.setLong(nextParamIndex++, maxEnergyStored());
		return nextParamIndex;
	}

	@Override
	public double getDouble(String columnName) {
		if("energy_stored".equals(columnName)) {
			return energyStored();
		}
		if("max_energy_stored".equals(columnName)) {
			return maxEnergyStored();
		}
		return 0;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, EnergyStorageData::energyStored,
		ByteBufCodecs.VAR_LONG, EnergyStorageData::maxEnergyStored,
		EnergyStorageData::new
	);
}
