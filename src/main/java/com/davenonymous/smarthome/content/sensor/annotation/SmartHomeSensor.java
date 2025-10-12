package com.davenonymous.smarthome.content.sensor.annotation;

import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.settings.SensorSettings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SmartHomeSensor {
	String modid();

	Class<? extends ISensorData> data();
	Class<? extends SensorSettings> settings();
}
