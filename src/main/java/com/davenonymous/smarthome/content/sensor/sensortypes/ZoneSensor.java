package com.davenonymous.smarthome.content.sensor.sensortypes;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public interface ZoneSensor<D extends ISensorData, T extends SensorSettings> extends HomeSensor<D, T> {

	List<D> visitZone(ServerLevel server, HomeZone zone, ConfiguredDevice device, T settings);
}
