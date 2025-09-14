package com.davenonymous.smarthome.lib.gui.event;

public record MouseDraggedEvent(double mouseX, double mouseY, int button, double dragX, double dragY) implements IEvent {
}
