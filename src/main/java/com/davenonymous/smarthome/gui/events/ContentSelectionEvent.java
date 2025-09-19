package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.lib.gui.event.IEvent;
import net.minecraft.resources.ResourceLocation;

public record ContentSelectionEvent(ResourceLocation contentId) implements IEvent {
}
