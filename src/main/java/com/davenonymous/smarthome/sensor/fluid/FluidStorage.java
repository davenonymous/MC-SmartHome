package com.davenonymous.smarthome.sensor.fluid;

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
import com.davenonymous.smarthome.api.sensor.settings.SidedOnOffSettings;
import com.davenonymous.smarthome.sensor.energy.EnergyStorageData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@SmartHomeSensor(modid = "minecraft", data = FluidStorageData.class, settings = SidedOnOffSettings.class)
public class FluidStorage implements BlockSensor<FluidStorageData, SidedOnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/fluid_storage");
	public static final BlockCapability<IFluidHandler, @Nullable Direction> FLUID = Capabilities.FluidHandler.BLOCK;

	@SensorName
	@I18DataGen(lang = "en_us", string = "Fluid Storage")
	@I18DataGen(lang = "de_de", string = "Flüssigkeitslager")
	public static final I18String SENSOR_NAME = SmartHome.dataString("sensor", "fluid_storage");

	@Override
	public SidedOnOffSettings getDefaultSettings() {
		return SidedOnOffSettings.DEFAULT;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return level.getCapability(FLUID, pos, null) != null;
	}

	@Override
	public boolean hasMin() {
		return true;
	}

	@Override
	public boolean usesDynamicMin() {
		return false;
	}

	@Override
	public double getStaticMin() {
		return 0;
	}

	@Override
	public boolean hasMax() {
		return true;
	}

	@Override
	public SensorColumn getMaxColumn() {
		return getColumns().get(2);
	}

	@Override
	public Optional<SensorColumn> getDefaultColumn() {
		return Optional.of(getColumns().get(1));
	}

	@Override
	public FluidStorageData visitZoneBlock(ServerLevel level, HomeZone zone, ConfiguredDevice device, SidedOnOffSettings settings, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		IFluidHandler cap = level.getCapability(FLUID, pos, settings.side().orElse(null));
		if(cap == null) {
			return null;
		}

		if(cap.getTanks() == 0) {
			return null;
		}

		FluidStack fluidInTank = cap.getFluidInTank(0);
		if(fluidInTank.isEmpty()) {
			return null;
		}


		var fluid = fluidInTank.getFluid();
		var fluidName = fluid.getFluidType().getDescriptionId(fluidInTank);
		return new FluidStorageData(fluidName, fluidInTank.getAmount(), cap.getTankCapacity(0));
	}

	@Override
	public double valueFromData(FluidStorageData data, SensorColumn column) {
		var columns = getColumns();
		if(columns.get(1).name().equals(column.name())) {
			return data.stored();
		}
		if(columns.get(2).name().equals(column.name())) {
			return data.capacity();
		}
		return 0;
	}

	@Override
	public FluidStorageData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new FluidStorageData(
			resultSet.getString(getColumns().get(0).name()),
			resultSet.getLong(getColumns().get(1).name()),
			resultSet.getLong(getColumns().get(2).name())
		);
	}
}
