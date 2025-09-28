package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanelWithValue;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;

public class WidgetToggle extends WidgetPanelWithValue<Boolean> {
	private WidgetSprite toggleSprite;

	public WidgetToggle(Boolean value) {
		super(value);
		toggleSprite = new WidgetSprite(GuiTheme.SpriteComponent.getToggleSprite(value), 0xFFFFFFFF);
		this.setSize(toggleSprite.width, toggleSprite.height);
		this.add(toggleSprite);

		this.addListener(ValueChangedEvent.class, (event, widget) -> {
			toggleSprite.setSprite(SmartHome.sprite(GuiTheme.SpriteComponent.getToggleSprite((Boolean)event.newValue)));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.addListener(MouseClickEvent.class, (event, widget) -> {
			if(event.button != 0) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			this.setValue(!this.getValue());
			this.fireEvent(new ValueChangedEvent<>(!this.getValue(), this.getValue()));
			return WidgetEventResult.HANDLED;
		});
	}
}
