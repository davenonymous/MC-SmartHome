package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.gui.event.IEvent;

public record DeviceSelectionEvent(HomeZone zone, ConfiguredDevice device) implements IEvent {
}
