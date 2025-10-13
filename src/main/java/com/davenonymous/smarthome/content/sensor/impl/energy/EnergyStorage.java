package com.davenonymous.smarthome.content.sensor.impl.energy;

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
import com.davenonymous.smarthome.content.sensor.settings.SidedOnOffSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SmartHomeSensor(modid = "minecraft", data = EnergyStorageData.class, settings = SidedOnOffSettings.class)
public class EnergyStorage implements BlockSensor<EnergyStorageData, SidedOnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/energy_storage");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Energy Storage")
	@I18DataGen(lang = "de_de", string = "Energielager")
	public static final I18String SENSOR_NAME = I18String.data("sensor.energy_storage", "name");

	@SensorDescription
	@I18DataGen(lang = "en_us", string = "Measures the energy stored in a block.")
	@I18DataGen(lang = "de_de", string = "Misst die in einem Block gespeicherte Energie.")
	public static final I18String SENSOR_DESCRIPTION = I18String.data("sensor.energy_storage", "description");

	public static final BlockCapability<IEnergyStorage, @Nullable Direction> ENERGY = Capabilities.EnergyStorage.BLOCK;

	@Override
	public SidedOnOffSettings getDefaultSettings() {
		return SidedOnOffSettings.DEFAULT;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return level.getCapability(ENERGY, pos, null) != null;
	}

	@Override
	public SensorRange getRange() {
		return new SensorRange.StaticMinDynamicMax(0, getColumn(1));
	}

	@Override
	public List<EnergyStorageData> visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, SidedOnOffSettings settings, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		IEnergyStorage cap = level.getCapability(ENERGY, pos, settings.side().orElse(null));
		if(cap == null) {
			return null;
		}

		long stored = cap.getEnergyStored();
		long max = cap.getMaxEnergyStored();

		return List.of(new EnergyStorageData(stored, max));
	}

	@Override
	public EnergyStorageData dataFromResultSet(ResultSet resultSet) throws SQLException {
		var columns = getColumns();
		return new EnergyStorageData(
			resultSet.getLong(columns.get(0).name()),
			resultSet.getLong(columns.get(1).name())
		);
	}
}
