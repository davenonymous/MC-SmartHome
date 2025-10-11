package com.davenonymous.smarthome.lib.gui.configurable;

import com.davenonymous.smarthome.lib.gui.NativeWidgetHelper;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetNativeWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EditBoxWidget<T> extends WidgetNativeWidget<BetterEditBox> {
	T value;

	boolean changeOnFocusLost = false;

	public EditBoxWidget(T value) {
		super(NativeWidgetHelper.createEditBox());
		this.value = value;
		this.setHeight(12);

		nativeWidget.setValue(formatAsString(this.value));
		nativeWidget.setHint(Component.literal(formatAsString(this.value)));
		nativeWidget.moveCursorToEnd(true);
		if(mustMatchRegex() != null) {
			nativeWidget.setFilter(input -> input.matches(mustMatchRegex()));
		}
		nativeWidget.setResponder(input -> {
			if(changeOnFocusLost) {
				return;
			}
			try {
				T newValue = parseValue(input == null ? "" : input.trim());
				if (newValue != null && !newValue.equals(this.value)) {
					var oldValue = this.value;
					this.value = newValue;
					fireEvent(new ValueChangedEvent<>(oldValue, newValue));
				}
			} catch (Exception e) {
				// Ignore invalid input
			}
		});
		nativeWidget.setFocusResponder(editBoxFocused -> {
			if(!changeOnFocusLost || editBoxFocused) {
				return;
			}
			try {
				T newValue = parseValue(nativeWidget().getValue().trim());
				if (newValue != null && !newValue.equals(this.value)) {
					var oldValue = this.value;
					this.value = newValue;
					fireEvent(new ValueChangedEvent<>(oldValue, newValue));
				}
			} catch (Exception e) {
				// Ignore invalid input
			}
		});
	}

	public EditBoxWidget<T> setChangeOnFocusLost(boolean changeOnFocusLost) {
		this.changeOnFocusLost = changeOnFocusLost;
		return this;
	}

	public void setValue(T value) {
		this.value = value;
		nativeWidget.setValue(formatAsString(this.value));
		nativeWidget.setHint(Component.literal(formatAsString(this.value)));
		nativeWidget.moveCursorToEnd(true);
	}

	public T getValue() {
		return value;
	}

	public abstract @NotNull String formatAsString(@NotNull T value);

	public abstract @Nullable T parseValue(@NotNull String input);

	public @Nullable String mustMatchRegex() {
		return null;
	}
}
