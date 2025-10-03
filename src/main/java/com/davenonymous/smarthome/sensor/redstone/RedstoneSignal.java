package com.davenonymous.smarthome.sensor.redstone;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.SensorRange;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.api.sensor.sensortypes.BlockSensor;
import com.davenonymous.smarthome.api.sensor.annotations.SensorId;
import com.davenonymous.smarthome.api.sensor.annotations.SensorName;
import com.davenonymous.smarthome.api.sensor.annotations.SmartHomeSensor;
import com.davenonymous.smarthome.api.sensor.settings.OnOffSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.ResultSet;
import java.sql.SQLException;

@SmartHomeSensor(modid = "minecraft", data = RedstoneSignalData.class, settings = OnOffSettings.class)
public class RedstoneSignal implements BlockSensor<RedstoneSignalData, OnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/redstone_signal");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Redstone Signal")
	@I18DataGen(lang = "de_de", string = "Redstone Signal")
	public static final I18String SENSOR_NAME = SmartHome.dataString("sensor", "redstone_signal");

	@Override
	public OnOffSettings getDefaultSettings() {
		return OnOffSettings.DEFAULT;
	}

	@Override
	public boolean isGeneric() {
		return true;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return !state.isAir();
	}

	@Override
	public SensorRange getRange() {
		return new SensorRange.StaticMinMax(0, 15);
	}

	@Override
	public RedstoneSignalData visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, OnOffSettings settings, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		int outputSignal = 0;
		if(state.hasAnalogOutputSignal()) {
			outputSignal = state.getAnalogOutputSignal(level, pos);
		}

		int neighborSignal = level.getBestNeighborSignal(pos);
		return new RedstoneSignalData(outputSignal, neighborSignal);
	}

	@Override
	public RedstoneSignalData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new RedstoneSignalData(
			resultSet.getInt(getColumn(0).name()),
			resultSet.getInt(getColumn(1).name())
		);
	}
}
