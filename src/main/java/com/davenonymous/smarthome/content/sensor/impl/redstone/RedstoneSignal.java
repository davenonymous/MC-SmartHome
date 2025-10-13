package com.davenonymous.smarthome.content.sensor.impl.redstone;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.SensorRange;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDescription;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.content.sensor.sensortypes.BlockSensor;
import com.davenonymous.smarthome.content.sensor.annotation.SensorId;
import com.davenonymous.smarthome.content.sensor.annotation.SensorName;
import com.davenonymous.smarthome.content.sensor.annotation.SmartHomeSensor;
import com.davenonymous.smarthome.content.sensor.settings.OnOffSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SmartHomeSensor(modid = "minecraft", data = RedstoneSignalData.class, settings = OnOffSettings.class)
public class RedstoneSignal implements BlockSensor<RedstoneSignalData, OnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/redstone_signal");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Redstone Signal")
	@I18DataGen(lang = "de_de", string = "Redstone Signal")
	public static final I18String SENSOR_NAME = I18String.data("sensor", "redstone_signal");

	@SensorDescription
	@I18DataGen(lang = "en_us", string = "Measures the redstone signal strength of a block.")
	@I18DataGen(lang = "de_de", string = "Misst die Redstone-Signalstärke eines Blocks.")
	public static final I18String SENSOR_DESCRIPTION = I18String.data("sensor", "redstone_signal_description");

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
	public List<RedstoneSignalData> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, OnOffSettings settings, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		int outputSignal = 0;
		if(state.hasAnalogOutputSignal()) {
			outputSignal = state.getAnalogOutputSignal(level, pos);
		}

		int neighborSignal = level.getBestNeighborSignal(pos);
		return List.of(new RedstoneSignalData(outputSignal, neighborSignal));
	}

	@Override
	public RedstoneSignalData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new RedstoneSignalData(
			resultSet.getInt(getColumn(0).name()),
			resultSet.getInt(getColumn(1).name())
		);
	}
}
