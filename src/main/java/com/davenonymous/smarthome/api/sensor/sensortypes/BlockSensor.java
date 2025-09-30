package com.davenonymous.smarthome.api.sensor.sensortypes;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockSensor<D extends ISensorData, T extends SensorSettings> extends HomeSensor<D, T> {

	D visitZoneBlock(ServerLevel server, HomeZone zone, ConfiguredDevice device, T settings, BlockPos pos, BlockState state, BlockEntity blockEntity);

	default boolean shouldVisitAllBlocksInZone() {
		return false;
	}
}
