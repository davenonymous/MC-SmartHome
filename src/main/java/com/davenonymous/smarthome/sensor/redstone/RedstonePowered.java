package com.davenonymous.smarthome.sensor.redstone;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.SensorColumn;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.ResultSet;
import java.sql.SQLException;

@SmartHomeSensor(modid = "minecraft", data = RedstonePoweredData.class, settings = OnOffSettings.class)
public class RedstonePowered implements BlockSensor<RedstonePoweredData, OnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/redstone");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Redstone Signal")
	@I18DataGen(lang = "de_de", string = "Redstone Signal")
	public static final I18String SENSOR_NAME = SmartHome.dataString("sensor", "redstone");

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
	public RedstonePoweredData visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, OnOffSettings settings, BlockPos pos, BlockState state, BlockEntity blockEntity) {

		int signal;
		if(state.hasAnalogOutputSignal()) {
			signal = state.getAnalogOutputSignal(level, pos);
		} else if(state.isRedstoneConductor(level, pos)) {
			signal = level.getBestNeighborSignal(pos);
		} else if(level.hasNeighborSignal(pos)){
			signal = level.getBestNeighborSignal(pos);
		} else {
			signal = 0;
		}

		return new RedstonePoweredData(signal);
	}

	@Override
	public RedstonePoweredData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new RedstonePoweredData(
			resultSet.getInt(getColumns().getFirst().name())
		);
	}

	@Override
	public double valueFromData(RedstonePoweredData data, SensorColumn column) {
		var columns = getColumns();
		if(columns.get(0).name().equals(column.name())) {
			return data.redstoneLevel();
		}
		return 0;
	}
}
