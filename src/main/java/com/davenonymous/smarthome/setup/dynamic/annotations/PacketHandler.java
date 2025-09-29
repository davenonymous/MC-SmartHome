package com.davenonymous.smarthome.setup.dynamic.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketHandler {
	Receiver value();

	enum Receiver {
		Server,
		Client,
		Both
	}
}
