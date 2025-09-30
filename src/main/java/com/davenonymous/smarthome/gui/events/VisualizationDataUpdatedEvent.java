package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.lib.gui.event.IEvent;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.UUID;

public record VisualizationDataUpdatedEvent(UUID deviceId, ResourceLocation sensorId, ResourceLocation vizId, LinkedHashMap<Pair<Instant, Long>, ISensorData> data) implements IEvent {
}
