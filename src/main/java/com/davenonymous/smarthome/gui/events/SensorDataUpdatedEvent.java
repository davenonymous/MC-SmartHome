package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.lib.gui.event.IEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record SensorDataUpdatedEvent(UUID deviceId, Map<ResourceLocation, ISensorData> data) implements IEvent {
}
