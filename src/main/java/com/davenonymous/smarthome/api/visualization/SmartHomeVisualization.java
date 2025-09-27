package com.davenonymous.smarthome.api.visualization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SmartHomeVisualization {
	String modid();
}
