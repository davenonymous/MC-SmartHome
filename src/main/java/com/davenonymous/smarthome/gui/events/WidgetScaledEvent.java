package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.lib.gui.event.IEvent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;

import java.util.UUID;

public record WidgetScaledEvent(Widget elementWidget, UUID elementId) implements IEvent {
}
