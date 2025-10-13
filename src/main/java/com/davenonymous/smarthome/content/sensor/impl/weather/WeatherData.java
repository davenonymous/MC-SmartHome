package com.davenonymous.smarthome.content.sensor.impl.weather;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDataColumnLabel;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDataStreamCodec;
import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public record WeatherData(float rainLevel, float thunderLevel, int timeToClear, int timeToRain, int timeToThunder, boolean isRaining, boolean isThundering) implements ISensorData {

	@SensorDataColumnLabel("rainLevel")
	@I18DataGen(lang = "en_us", string = "Rain Level")
	@I18DataGen(lang = "de_de", string = "Regen Stärke")
	public static final I18String RAIN_COLUMN = I18String.data("sensor.weather.column", "rain_level");

	@SensorDataColumnLabel("thunderLevel")
	@I18DataGen(lang = "en_us", string = "Thunder Level")
	@I18DataGen(lang = "de_de", string = "Donner Stärke")
	public static final I18String THUNDER_COLUMN = I18String.data("sensor.weather.column", "thunder_level");

	@SensorDataColumnLabel("timeToClear")
	@I18DataGen(lang = "en_us", string = "Time to clear")
	@I18DataGen(lang = "de_de", string = "Zeit bis klar")
	public static final I18String TIME_TO_CLEAR_COLUMN = I18String.data("sensor.weather.column", "clear_time");

	@SensorDataColumnLabel("timeToRain")
	@I18DataGen(lang = "en_us", string = "Time to rain")
	@I18DataGen(lang = "de_de", string = "Zeit bis Regen")
	public static final I18String TIME_TO_RAIN_COLUMN = I18String.data("sensor.weather.column", "rain_time");

	@SensorDataColumnLabel("timeToThunder")
	@I18DataGen(lang = "en_us", string = "Time to thunder")
	@I18DataGen(lang = "de_de", string = "Zeit bis Gewitter")
	public static final I18String TIME_TO_THUNDER_COLUMN = I18String.data("sensor.weather.column", "thunder_time");

	@SensorDataColumnLabel("isRaining")
	@I18DataGen(lang = "en_us", string = "Raining")
	@I18DataGen(lang = "de_de", string = "Regen")
	public static final I18String IS_RAINING_COLUMN = I18String.data("sensor.weather.column", "is_raining");

	@SensorDataColumnLabel("isThundering")
	@I18DataGen(lang = "en_us", string = "Thundering")
	@I18DataGen(lang = "de_de", string = "Gewitter")
	public static final I18String IS_THUNDERING_COLUMN = I18String.data("sensor.weather.column", "is_thundering");


	@Override
	public Object[] columnValues() {
		return new Object[] {rainLevel, thunderLevel, timeToClear, timeToRain, timeToThunder, isRaining, isThundering};
	}

	@Override
	public String displayString() {
		return rainLevel + " / " + thunderLevel + " / " + timeToClear;
	}

	@Override
	public int bindParameters(PreparedStatement prepped, int nextParamIndex) throws SQLException {
		prepped.setFloat(nextParamIndex++, rainLevel());
		prepped.setFloat(nextParamIndex++, thunderLevel());
		prepped.setInt(nextParamIndex++, timeToClear());
		prepped.setInt(nextParamIndex++, timeToRain());
		prepped.setInt(nextParamIndex++, timeToThunder());
		prepped.setBoolean(nextParamIndex++, isRaining());
		prepped.setBoolean(nextParamIndex++, isThundering());
		return nextParamIndex;
	}

	@SensorDataStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, WeatherData> STREAM_CODEC = BiggerStreamCodec.composite(
		ByteBufCodecs.FLOAT, WeatherData::rainLevel,
		ByteBufCodecs.FLOAT, WeatherData::thunderLevel,
		ByteBufCodecs.INT, WeatherData::timeToClear,
		ByteBufCodecs.INT, WeatherData::timeToRain,
		ByteBufCodecs.INT, WeatherData::timeToThunder,
		ByteBufCodecs.BOOL, WeatherData::isRaining,
		ByteBufCodecs.BOOL, WeatherData::isThundering,
		WeatherData::new
	);
}
