package com.davenonymous.smarthome.api.sensor;

public class SensorLoadException extends RuntimeException {
	public SensorLoadException(String message) {
		super(message);
	}

	public SensorLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
