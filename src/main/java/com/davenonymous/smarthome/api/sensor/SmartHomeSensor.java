package com.davenonymous.smarthome.api.sensor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SmartHomeSensor {
	String modid();
}
