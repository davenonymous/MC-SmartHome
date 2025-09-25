package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.DeviceSelectionEvent;
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
	public DeviceDetailWidget deviceDetail;

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
		configuredDevicesTable.addListener(
			DeviceSelectionEvent.class, (event, widget) -> {
			if(deviceDetail.device() != null && deviceDetail.device().equals(event.device())) {
				// Deselect if the same device is clicked again
				deviceDetail.setDevice(null, null);
				updateWidgetSizes();
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			deviceDetail.setDevice(event.zone(), event.device());
			updateWidgetSizes();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		this.add(configuredDevicesTable);

		deviceDetail = new DeviceDetailWidget();
		this.add(deviceDetail);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			newDevicesBar.updateDevices(HomeScreen.get().getAllNewDevices());
			configuredDevicesTable.updateDevices();
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

		boolean hasNewDevices = !newDevicesBar.children().isEmpty();
		if(!hasNewDevices) {
			devicesLabel.setPosition(8, 8);
			configuredDevicesTable.setPosition(8, 24);
			configuredDevicesTable.setHeight(this.height - 34);
		} else {
			devicesLabel.setPosition(8, newDevicesBar.y + newDevicesBar.height + 8);
			configuredDevicesTable.setPosition(8, devicesLabel.y + devicesLabel.height + 18);
			configuredDevicesTable.setHeight(this.height - configuredDevicesTable.y - 8);
			configuredDevicesTable.updateWidgetSizes();
		}

		int displayWidth = this.width() - 16;
		int displayX = 8;
		if(deviceDetail.device() != null) {
			displayWidth = (int)(this.width() * 2 / 3f);
			int detailX = displayX + displayWidth + 5;
			int detailWidth = this.width() - displayWidth - 21;

			deviceDetail.setPosition(detailX, configuredDevicesTable.y);
			deviceDetail.setWidth(detailWidth);
			deviceDetail.setHeight(configuredDevicesTable.height());
			deviceDetail.setVisible(true);
			deviceDetail.updateWidgetSizes();
		} else {
			deviceDetail.setVisible(false);
		}

		configuredDevicesTable.setWidth(displayWidth);

		if(!hasNewDevices) {
			newDevicesBar.setVisible(false);
			newDevicesLabel.setVisible(false);


		} else {
			newDevicesBar.setVisible(true);
			newDevicesBar.setWidth(displayWidth - 16);
			newDevicesBar.updateWidgetSizes();
			newDevicesLabel.setVisible(true);


		}

	}
}
