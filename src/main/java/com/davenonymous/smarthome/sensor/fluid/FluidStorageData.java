package com.davenonymous.smarthome.sensor.fluid;

import com.davenonymous.smarthome.SmartHome;
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

public record FluidStorageData(String fluidId, long stored, long capacity) implements ISensorData {

	@SensorDataColumnLabel("fluidId")
	@I18DataGen(lang = "en_us", string = "Fluid")
	@I18DataGen(lang = "de_de", string = "Flüssigkeit")
	public static final I18String FLUID_ID = SmartHome.dataString("sensor.fluid_storage.column", "fluid");

	@SensorDataColumnLabel("stored")
	@I18DataGen(lang = "en_us", string = "Stored")
	@I18DataGen(lang = "de_de", string = "Vorhanden")
	public static final I18String FLUID_STORED = SmartHome.dataString("sensor.fluid_storage.column", "stored");

	@SensorDataColumnLabel("capacity")
	@I18DataGen(lang = "en_us", string = "Capacity")
	@I18DataGen(lang = "de_de", string = "Kapazität")
	public static final I18String FLUID_CAPACITY = SmartHome.dataString("sensor.fluid_storage.column", "capacity");

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
