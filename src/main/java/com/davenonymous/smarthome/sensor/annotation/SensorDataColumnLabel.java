package com.davenonymous.smarthome.sensor.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SensorDataColumnLabel {
	String value();
}
