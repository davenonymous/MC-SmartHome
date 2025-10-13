package com.davenonymous.smarthome.content.sensor.impl.weather;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.SensorRange;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDescription;
import com.davenonymous.smarthome.content.sensor.annotation.SensorId;
import com.davenonymous.smarthome.content.sensor.annotation.SensorName;
import com.davenonymous.smarthome.content.sensor.annotation.SmartHomeSensor;
import com.davenonymous.smarthome.content.sensor.impl.redstone.RedstoneSignalData;
import com.davenonymous.smarthome.content.sensor.sensortypes.BlockSensor;
import com.davenonymous.smarthome.content.sensor.sensortypes.ZoneSensor;
import com.davenonymous.smarthome.content.sensor.settings.OnOffSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;

import java.sql.ResultSet;
import java.sql.SQLException;

@SmartHomeSensor(modid = "minecraft", data = WeatherData.class, settings = OnOffSettings.class)
public class Weather implements ZoneSensor<WeatherData, OnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/weather");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Weather")
	@I18DataGen(lang = "de_de", string = "Wetter")
	public static final I18String SENSOR_NAME = I18String.data("sensor", "weather");

	@SensorDescription
	@I18DataGen(lang = "en_us", string = "Measures the current weather conditions")
	@I18DataGen(lang = "de_de", string = "Misst die aktuellen Wetterbedingungen")
	public static final I18String SENSOR_DESCRIPTION = I18String.data("sensor", "weather_description");

	@Override
	public OnOffSettings getDefaultSettings() {
		return OnOffSettings.DEFAULT;
	}

	@Override
	public int lowestAllowedTickRate() {
		return 20 * 60; // 1 minute
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return state.is(Blocks.DAYLIGHT_DETECTOR);
	}

	@Override
	public WeatherData visitZone(ServerLevel level, HomeZone zone, ConfiguredDevice device, OnOffSettings settings) {
		float rainLevel = level.getRainLevel(0);
		float thunderLevel = level.getThunderLevel(0);
		int timeToClear = ((ServerLevelData)level.getLevelData()).getClearWeatherTime();
		int timeToRain = ((ServerLevelData)level.getLevelData()).getRainTime();
		int timeToThunder = ((ServerLevelData)level.getLevelData()).getThunderTime();
		boolean isRaining = level.isRaining();
		boolean isThundering = level.isThundering();

		return new WeatherData(rainLevel, thunderLevel, timeToClear, timeToRain, timeToThunder, isRaining, isThundering);
	}

	@Override
	public WeatherData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new WeatherData(
			resultSet.getFloat(getColumn(0).name()),
			resultSet.getFloat(getColumn(1).name()),
			resultSet.getInt(getColumn(2).name()),
			resultSet.getInt(getColumn(3).name()),
			resultSet.getInt(getColumn(4).name()),
			resultSet.getBoolean(getColumn(5).name()),
			resultSet.getBoolean(getColumn(6).name())
		);
	}
}
