package com.davenonymous.smarthome.api.sensor.sensortypes;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.server.level.ServerLevel;

public interface ZoneSensor<D extends ISensorData, T extends SensorSettings> extends HomeSensor<D, T> {

	D visitZone(ServerLevel server, HomeZone zone, ConfiguredDevice device, T settings);
}
