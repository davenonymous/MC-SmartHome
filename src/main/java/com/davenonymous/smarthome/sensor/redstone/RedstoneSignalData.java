package com.davenonymous.smarthome.sensor.redstone;

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

public record RedstoneSignalData(int outputLevel, int maxNeighborLevel) implements ISensorData {

	@SensorDataColumnLabel("outputLevel")
	@I18DataGen(lang = "en_us", string = "Output")
	@I18DataGen(lang = "de_de", string = "Ausgang")
	public static final I18String OUTPUT_COLUMN = SmartHome.dataString("sensor.redstone_signal.column", "output_level");

	@SensorDataColumnLabel("maxNeighborLevel")
	@I18DataGen(lang = "en_us", string = "Input")
	@I18DataGen(lang = "de_de", string = "Eingang")
	public static final I18String MAX_NEIGHBOR_COLUMN = SmartHome.dataString("sensor.redstone_signal.column", "max_neighbor_level");

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
