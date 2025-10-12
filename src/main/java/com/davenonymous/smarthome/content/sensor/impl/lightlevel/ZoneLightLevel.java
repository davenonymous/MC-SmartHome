package com.davenonymous.smarthome.content.sensor.impl.lightlevel;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.SensorRange;
import com.davenonymous.smarthome.content.sensor.annotation.SensorDescription;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.content.sensor.sensortypes.ZoneSensor;
import com.davenonymous.smarthome.content.sensor.annotation.SensorId;
import com.davenonymous.smarthome.content.sensor.annotation.SensorName;
import com.davenonymous.smarthome.content.sensor.annotation.SmartHomeSensor;
import com.davenonymous.smarthome.content.sensor.settings.OnOffSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.ResultSet;
import java.sql.SQLException;

@SmartHomeSensor(modid = SmartHome.MODID, data = ZoneLightLevelData.class, settings = OnOffSettings.class)
public class ZoneLightLevel implements ZoneSensor<ZoneLightLevelData, OnOffSettings> {
	@SensorId
	public static final ResourceLocation SENSOR_ID = SmartHome.resource("sensor/block_light");

	@SensorName
	@I18DataGen(lang = "en_us", string = "Light Level")
	@I18DataGen(lang = "de_de", string = "Lichtstärke")
	public static final I18String SENSOR_NAME = I18String.data("sensor", "block_light");

	@SensorDescription
	@I18DataGen(lang = "en_us", string = "Measures the light level in a zone.")
	@I18DataGen(lang = "de_de", string = "Misst die Lichtstärke in einer Zone.")
	public static final I18String SENSOR_DESCRIPTION = I18String.data("sensor", "block_light_description");

	@Override
	public OnOffSettings getDefaultSettings() {
		return OnOffSettings.DEFAULT;
	}

	@Override
	public boolean isValid(Level level, BlockPos pos, BlockState state) {
		return state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.REDSTONE_WALL_TORCH);
	}

	@Override
	public SensorRange getRange() {
		return new SensorRange.StaticMinMax(0, 15);
	}

	@Override
	public ZoneLightLevelData visitZone(ServerLevel server, HomeZone zone, ConfiguredDevice device, OnOffSettings settings) {
		var bounds = zone.bounds();
		int yMax = (int) bounds.maxY;
		int xMin = (int) bounds.minX;
		int xMax = (int) bounds.maxX;
		int zMin = (int) bounds.minZ;
		int zMax = (int) bounds.maxZ;
		int totalLight = 0;
		int totalBlocks = 0;
		int minLight = Integer.MAX_VALUE;
		int maxLight = Integer.MIN_VALUE;
		for(int x = xMin; x <= xMax; x++) {
			for(int z = zMin; z <= zMax; z++) {
				int y = (int) bounds.minY;
				while(y <= yMax) {
					var pos = new BlockPos(x, y, z);
					var state = server.getBlockState(pos);
					if(!state.isAir()) {
						y++;
						continue;
					}

					int lightLevel = server.getBrightness(LightLayer.BLOCK, pos);
					minLight = Math.min(minLight, lightLevel);
					maxLight = Math.max(maxLight, lightLevel);
					totalLight += lightLevel;
					totalBlocks++;
					break;
				}
			}
		}

		if(totalBlocks == 0 || totalLight == 0) {
			return new ZoneLightLevelData(0, 0, 0);
		}

		double avgLight = (double) totalLight / (double) totalBlocks;
		return new ZoneLightLevelData(minLight, maxLight, avgLight);
	}

	@Override
	public ZoneLightLevelData dataFromResultSet(ResultSet resultSet) throws SQLException {
		return new ZoneLightLevelData(
			resultSet.getInt(getColumns().get(0).name()),
			resultSet.getInt(getColumns().get(1).name()),
			resultSet.getDouble(getColumns().get(2).name())
		);
	}
}
