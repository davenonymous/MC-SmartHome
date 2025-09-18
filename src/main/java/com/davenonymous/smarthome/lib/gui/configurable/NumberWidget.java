package com.davenonymous.smarthome.lib.gui.configurable;

import com.davenonymous.smarthome.lib.gui.event.MouseScrollEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import org.jetbrains.annotations.NotNull;

public abstract class NumberWidget<T extends Number> extends EditBoxWidget<T> {

	public NumberWidget(T value) {
		super(value);
		this.value = value;
		this.setHeight(12);

		this.addListener(MouseScrollEvent.class, (event, widget) -> {
			if (widget.isHovered() && event.rawScrollValue != 0) {
				double delta = event.rawScrollValue > 0 ? 1 : -1;
				var oldValue = this.value;
				T newValue = add(this.value, delta);
				nativeWidget.setValue(formatAsString(newValue));
				this.value = newValue;
				fireEvent(new ValueChangedEvent<>(this.value, oldValue));
				return WidgetEventResult.HANDLED;
			}
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public abstract @NotNull T add(T value, double delta);

}
