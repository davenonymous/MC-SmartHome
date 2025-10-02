package com.davenonymous.smarthome.sensor.lightlevel;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.api.sensor.annotations.SensorDataStreamCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record ZoneLightLevelData(short minLevel, short maxLevel, double avgLevel) implements ISensorData {
	public ZoneLightLevelData(int minLevel, int maxLevel, double avgLevel) {
		this((short)minLevel, (short)maxLevel, avgLevel);
	}

	@Override
	public Object[] columnValues() {
		return new Object[] {minLevel, maxLevel, avgLevel};
	}

	@Override
	public String displayString() {
		return String.format("%.2f", avgLevel);
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setShort(nextParamIndex++, minLevel);
		prepped.setShort(nextParamIndex++, maxLevel);
		prepped.setDouble(nextParamIndex++, avgLevel);
		return nextParamIndex;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, ZoneLightLevelData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.SHORT, ZoneLightLevelData::minLevel,
		ByteBufCodecs.SHORT, ZoneLightLevelData::maxLevel,
		ByteBufCodecs.DOUBLE, ZoneLightLevelData::avgLevel,
		ZoneLightLevelData::new
	);

	@I18DataGen(lang = "en_us", string = "Minimum Zone Light Level")
	@I18DataGen(lang = "de_de", string = "Minimale Zonen-Lichtstärke")
	public static final I18String MIN_SENSOR_COLUMN = SmartHome.dataString("sensor.block_light", "min_zone_light");

	@I18DataGen(lang = "en_us", string = "Maximum Zone Light Level")
	@I18DataGen(lang = "de_de", string = "Maximale Zonen-Lichtstärke")
	public static final I18String MAX_SENSOR_COLUMN = SmartHome.dataString("sensor.block_light", "max_zone_light");

	@I18DataGen(lang = "en_us", string = "Average Zone Light Level")
	@I18DataGen(lang = "de_de", string = "Durchschnittliche Zonen-Lichtstärke")
	public static final I18String AVG_SENSOR_COLUMN = SmartHome.dataString("sensor.block_light", "avg_zone_light");
}
