package com.davenonymous.smarthome.lib.i18n;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Repeatable(value = I18DataGens.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface I18DataGen {
	String lang();
	String string();
}
