package com.davenonymous.smarthome.gui.events;

import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.lib.gui.event.IEvent;

import java.util.UUID;

public record ElementSettingsChangedEvent(UUID id, HomeCardElement<?> newCardElement) implements IEvent {
}
