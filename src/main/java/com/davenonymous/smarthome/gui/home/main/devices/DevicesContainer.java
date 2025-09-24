package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

public class DevicesContainer extends WidgetPanel {
	public WidgetTextBox newDevicesLabel;
	public NewDevicesWidget newDevicesBar;

	public WidgetTextBox devicesLabel;
	public ConfiguredDevicesTable configuredDevicesTable;

	public DevicesContainer() {
		newDevicesBar = new NewDevicesWidget();
		newDevicesBar.setPosition(8, 20);
		newDevicesBar.setHeight(110);
		newDevicesBar.updateDevices(HomeScreen.get().getAllNewDevices());
		this.add(newDevicesBar);

		newDevicesLabel = new WidgetTextBox(I18n.get("smarthome.gui.home.devices.label.new_devices"));
		newDevicesLabel.setFont(ModFonts.BASEL);
		newDevicesLabel.autoWidth();
		newDevicesLabel.autoHeight();
		newDevicesLabel.setPosition(8, 8);
		newDevicesLabel.setTextColor(0xFFFFFFFF);
		this.add(newDevicesLabel);

		devicesLabel = new WidgetTextBox(I18n.get("smarthome.gui.home.devices.label.configured_devices"));
		devicesLabel.setFont(ModFonts.BASEL);
		devicesLabel.autoWidth();
		devicesLabel.autoHeight();
		devicesLabel.setPosition(8, 140);
		devicesLabel.setTextColor(0xFFFFFFFF);
		this.add(devicesLabel);

		configuredDevicesTable = new ConfiguredDevicesTable();
		configuredDevicesTable.setPosition(8, 152);
		this.add(configuredDevicesTable);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			newDevicesBar.updateDevices(HomeScreen.get().getAllNewDevices());
			updateWidgetSizes();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		updateWidgetSizes();
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		super.draw(guiGraphics, window);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		configuredDevicesTable.setWidth(this.width - 16);

		if(newDevicesBar.children().isEmpty()) {
			newDevicesBar.setVisible(false);
			newDevicesLabel.setVisible(false);

			devicesLabel.setPosition(8, 8);
			configuredDevicesTable.setPosition(8, 24);
			configuredDevicesTable.setHeight(this.height - 34);
		} else {
			newDevicesBar.setVisible(true);
			newDevicesBar.setWidth(this.width - 16);
			newDevicesBar.updateWidgetSizes();
			newDevicesLabel.setVisible(true);

			devicesLabel.setPosition(8, newDevicesBar.y + newDevicesBar.height + 8);
			configuredDevicesTable.setPosition(8, devicesLabel.y + devicesLabel.height + 18);
			configuredDevicesTable.setHeight(this.height - configuredDevicesTable.y - 8);
			configuredDevicesTable.updateWidgetSizes();
		}

	}
}
