package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.gui.general.ScaleHandle;
import com.davenonymous.smarthome.lib.gui.event.IEvent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;

public record ScaleHandleResizedEvent(ScaleHandle scaleHandle, Widget attachedTo) implements IEvent {
}
