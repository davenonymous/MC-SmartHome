package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.lib.gui.event.IEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record VisualizationDataUpdatedEvent(UUID deviceId, ResourceLocation sensorId, ResourceLocation vizId, IVisualizationData data) implements IEvent {
}
