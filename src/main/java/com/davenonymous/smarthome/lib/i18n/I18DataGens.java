package com.davenonymous.smarthome.lib.i18n;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface I18DataGens {
	I18DataGen[] value();
}
