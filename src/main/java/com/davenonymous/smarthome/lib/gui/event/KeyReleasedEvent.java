package com.davenonymous.smarthome.lib.gui.event;

import com.mojang.blaze3d.platform.InputConstants;

public class KeyReleasedEvent implements IEvent {
	public int keyCode;
	public int scanCode;
	public int modifiers;

	public KeyReleasedEvent(int keyCode, int scanCode, int modifiers) {
		this.keyCode = keyCode;
		this.scanCode = scanCode;
		this.modifiers = modifiers;
	}

	@Override
	public String toString() {
		var key = InputConstants.getKey(keyCode, scanCode);
		return String.format("Key Released {%s: key=%d, scan=%d, modifiers=%d}", key.getName(), keyCode, scanCode, modifiers);
	}
}
