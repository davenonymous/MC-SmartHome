package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.lib.gui.event.IEvent;

public record CardSelectedEvent(HomeCard card) implements IEvent {
}
