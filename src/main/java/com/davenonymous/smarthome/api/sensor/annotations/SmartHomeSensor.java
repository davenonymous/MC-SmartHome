package com.davenonymous.smarthome.api.sensor.annotations;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SmartHomeSensor {
	String modid();

	Class<? extends ISensorData> data();
	Class<? extends SensorSettings> settings();
}
