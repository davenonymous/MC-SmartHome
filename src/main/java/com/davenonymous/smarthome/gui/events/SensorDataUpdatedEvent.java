package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.api.SensorData;
import com.davenonymous.smarthome.lib.gui.event.IEvent;

import java.util.List;
import java.util.UUID;

public record SensorDataUpdatedEvent(UUID deviceId, List<SensorData> data) implements IEvent {
}
