package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.lib.gui.event.IEvent;

import java.util.UUID;

public record ElementRemovedEvent(UUID id) implements IEvent {
}
