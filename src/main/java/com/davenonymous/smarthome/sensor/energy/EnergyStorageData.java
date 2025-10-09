package com.davenonymous.smarthome.sensor.energy;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.sensor.annotation.SensorDataColumnLabel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record EnergyStorageData(long energyStored, long maxEnergyStored) implements ISensorData {

	@SensorDataColumnLabel("energyStored")
	@I18DataGen(lang = "en_us", string = "Stored")
	@I18DataGen(lang = "de_de", string = "Vorhanden")
	public static final I18String ENERGY_STORED = I18String.data("sensor.energy_storage.column", "stored");

	@SensorDataColumnLabel("maxEnergyStored")
	@I18DataGen(lang = "en_us", string = "Capacity")
	@I18DataGen(lang = "de_de", string = "Kapazität")
	public static final I18String MAX_ENERGY_STORED = I18String.data("sensor.energy_storage.column", "capacity");

	@Override
	public Object[] columnValues() {
		return new Object[] {energyStored, maxEnergyStored};
	}

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

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, EnergyStorageData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG, EnergyStorageData::energyStored,
		ByteBufCodecs.VAR_LONG, EnergyStorageData::maxEnergyStored,
		EnergyStorageData::new
	);
}
