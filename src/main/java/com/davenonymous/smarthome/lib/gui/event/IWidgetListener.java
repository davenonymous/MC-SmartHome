package com.davenonymous.smarthome.lib.gui.event;


import com.davenonymous.smarthome.lib.gui.widgets.Widget;

public interface IWidgetListener<T extends IEvent> {
	WidgetEventResult call(T event, Widget widget);
}
