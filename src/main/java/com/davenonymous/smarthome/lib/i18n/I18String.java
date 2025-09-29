package com.davenonymous.smarthome.lib.i18n;

import net.minecraft.client.resources.language.I18n;

public record I18String(String key) {

	public I18String(String modId, String category, String id) {
		this(modId + ".gui." + category + "." + id);
	}

	public String get() {
		return I18n.get(this.key);
	}

	public String get(Object... parameters) {
		return I18n.get(this.key, parameters);
	}
}
