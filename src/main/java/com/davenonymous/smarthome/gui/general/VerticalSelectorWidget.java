package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanelWithValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class VerticalSelectorWidget<T> extends WidgetPanelWithValue<T> {
	private final List<T> choices;
	private final Function<T, Widget> choiceWidgetProvider;

	Widget selectedWidget;
	VerticalSelectorPopupWidget selector;

	public VerticalSelectorWidget(List<T> choices, T value, Function<T, Widget> choiceWidgetProvider) {
		super(value);
		this.choices = choices;
		this.choiceWidgetProvider = choiceWidgetProvider;

		this.selectedWidget = choiceWidgetProvider.apply(value);
		this.add(selectedWidget);

		this.setSize(selectedWidget.getWidth(), selectedWidget.getHeight());

		this.addListener(
			MouseClickEvent.class, (event, widget) -> {
				if(selector != null && getGUI().children().contains(selector)) {
					getGUI().remove(selector);
					selector = null;
				}

				if(this.choices.size() <= 1) {
					return WidgetEventResult.HANDLED;
				}

				List<Widget> choiceWidgets = new ArrayList<>();
				for(var choice : this.choices) {
					var choiceWidget = this.choiceWidgetProvider.apply(choice);
					if(choiceWidget == null) {
						continue;
					}
					choiceWidget.addListener(MouseClickEvent.class, (choiceClickEvent, choiceClickWidget) -> {
						this.setValue(choice);
						if(selector != null && getGUI().children().contains(selector)) {
							getGUI().remove(selector);
							selector = null;
						}
						return WidgetEventResult.HANDLED;
					});
					choiceWidgets.add(choiceWidget);
				}

				selector = VerticalSelectorPopupWidget.openAt(getMouseX(), getMouseY() + 32, ContentAlignment.BOTTOM_CENTER,
					choiceWidgets.toArray(new Widget[0])
				);
				selector.zLevel += 20;
				getGUI().add(selector);
				return WidgetEventResult.HANDLED;
			});
	}

	@Override
	public void setValue(T newValue) {
		super.setValue(newValue);
		this.selectedWidget = choiceWidgetProvider.apply(newValue);
		this.clear();
		this.add(selectedWidget);
	}
}
