package com.davenonymous.smarthome.sensor.occupancy;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.SensorColumn;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.api.sensor.sensortypes.EntitySensor;
import com.davenonymous.smarthome.api.sensor.annotations.SensorId;
import com.davenonymous.smarthome.api.sensor.annotations.SensorName;
import com.davenonymous.smarthome.api.sensor.annotations.SmartHomeSensor;
import com.davenonymous.smarthome.api.sensor.settings.OnOffSettings;
import com.davenonymous.smarthome.sensor.energy.EnergyStorageData;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.ResultSet;
import java.sql.SQLException;

@SmartHomeSensor(modid = "minecraft", data = OccupancyData.class, settings = OnOffSettings.class)
public class Occupancy implements EntitySensor<OccupancyData, OnOffSettings> {
	@SensorId
	public static final ResourceLocation ID = SmartHome.resource("sensor/occupancy");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Occupancy")
	@I18DataGen(lang = "de_de", string = "Anwesenheit")
	public static final I18String SENSOR_NAME = SmartHome.dataString("sensor", "occupancy");

	@Override
	public boolean isMultiRow() {
		return true;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return state.is(ModBlocks.DASHBOARD);
	}

	@Override
	public OnOffSettings getDefaultSettings() {
		return OnOffSettings.DEFAULT;
	}

	@Override
	public OccupancyData visitZoneEntity(ServerLevel level, HomeZone zone, ConfiguredDevice device, OnOffSettings settings, Entity entity) {
		if(!(entity instanceof LivingEntity livingEntity)) {
			return null;
		}

		var name = livingEntity.getName().getString();
		var type = livingEntity.getType().getDescriptionId();
		var category = livingEntity.getClassification(false).getName();
		var entityId = livingEntity.getId();
		return new OccupancyData(entityId, name, type, category, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
	}

	@Override
	public OccupancyData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new OccupancyData(
				resultSet.getInt(getColumns().get(0).name()),
				resultSet.getString(getColumns().get(1).name()),
				resultSet.getString(getColumns().get(2).name()),
				resultSet.getString(getColumns().get(3).name()),
				resultSet.getDouble(getColumns().get(4).name()),
				resultSet.getDouble(getColumns().get(5).name()),
				resultSet.getDouble(getColumns().get(6).name())
		);
	}
}
