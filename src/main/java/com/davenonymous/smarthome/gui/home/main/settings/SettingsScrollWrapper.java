package com.davenonymous.smarthome.gui.home.main.settings;

import com.davenonymous.smarthome.gui.general.ScissorScrollWrap;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;

public class SettingsScrollWrapper extends WidgetPanel {
	ScissorScrollWrap wrapper;
	SettingsContainer settingsContainer;

	public SettingsScrollWrapper() {
		super();

		settingsContainer = new SettingsContainer();
		settingsContainer.setSize(300, 300);
		wrapper = new ScissorScrollWrap(settingsContainer);
		this.add(wrapper);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		//wrapper.setPosition(0, 0);
		wrapper.setSize(this.width(), this.height());
		settingsContainer.adjustSizeToContent(false);
		settingsContainer.setWidth(this.width());
		settingsContainer.updateWidgetSizes();
		//settingsContainer.setPosition(0, 0);
	}
}
