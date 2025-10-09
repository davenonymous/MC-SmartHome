package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.DeviceSelectionEvent;
import com.davenonymous.smarthome.gui.home.main.devices.table.ConfiguredDevicesTable;
import com.davenonymous.smarthome.gui.home.main.devices.table.DeviceTableContainer;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.requests.RequestDeviceDataPayload;
import com.davenonymous.smarthome.networking.actions.requests.RequestVisualizationDataPayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public class DevicesContainer extends WidgetPanel {
	public WidgetTextBox newDevicesLabel;
	public NewDevicesWidget newDevicesBar;

	public WidgetTextBox devicesLabel;
	public ConfiguredDevicesTable configuredDevicesTable;
	public DeviceDetailWidget deviceDetail;
	public WidgetPanel tableContainer;

	@I18DataGen(lang = "en_us", string = "New Devices")
	@I18DataGen(lang = "de_de", string = "Neue Geräte")
	public static final I18String NEW_DEVICES = I18String.gui("home.devices", "label.new_devices");

	@I18DataGen(lang = "en_us", string = "Configured Devices")
	@I18DataGen(lang = "de_de", string = "Konfigurierte Geräte")
	public static final I18String CONFIGURED_DEVICES = I18String.gui("home.devices", "label.configured_devices");


	public DevicesContainer() {
		super();
		newDevicesBar = new NewDevicesWidget();
		newDevicesBar.setPosition(8, 20);
		newDevicesBar.setHeight(110);
		newDevicesBar.updateDevices(HomeScreen.get().getAllNewDevices());
		this.add(newDevicesBar);

		newDevicesLabel = new WidgetTextBox(NEW_DEVICES.get());
		newDevicesLabel.setFont(ModFonts.BASEL);
		newDevicesLabel.autoWidth();
		newDevicesLabel.autoHeight();
		newDevicesLabel.setPosition(8, 8);
		newDevicesLabel.setTextColor(0xFFFFFFFF);
		this.add(newDevicesLabel);

		devicesLabel = new WidgetTextBox(CONFIGURED_DEVICES.get());
		devicesLabel.setFont(ModFonts.BASEL);
		devicesLabel.autoWidth();
		devicesLabel.autoHeight();
		devicesLabel.setPosition(8, 140);
		devicesLabel.setTextColor(0xFFFFFFFF);
		this.add(devicesLabel);

		configuredDevicesTable = new ConfiguredDevicesTable();
		configuredDevicesTable.setPosition(8, 8);
		configuredDevicesTable.addListener(
			DeviceSelectionEvent.class, (event, widget) -> {
			if(deviceDetail.device() != null && deviceDetail.device().id().equals(event.device().id())) {
				// Deselect if the same device is clicked again
				deviceDetail.setDevice(null, null);
				updateWidgetSizes();
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			PacketDistributor.sendToServer(new RequestDeviceDataPayload(event.zone().home().id(), event.zone().id(), event.device()));

			for(var sensorId : event.device().sensors().keySet()) {
				var sensor = ModSensors.getById(sensorId);
				if(sensor == null) {
					continue;
				}
				ResourceLocation vizId = sensor.getDefaultVisualization();

				var vizPayload = new RequestVisualizationDataPayload(event.device(), sensorId, vizId, sensor.getDefaultVisualizationSettings(event.device()));
				PacketDistributor.sendToServer(vizPayload);
			}

			deviceDetail.setDevice(event.zone(), event.device());
			updateWidgetSizes();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		tableContainer = new DeviceTableContainer();
		tableContainer.setPosition(8, 152);
		tableContainer.add(configuredDevicesTable);

		this.add(tableContainer);

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
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		boolean hasNewDevices = !newDevicesBar.children().isEmpty();
		if(!hasNewDevices) {
			devicesLabel.setPosition(8, 8);
			tableContainer.setPosition(8, 24);
			tableContainer.setHeight(this.height - 34);
		} else {
			devicesLabel.setPosition(8, newDevicesBar.y + newDevicesBar.height + 8);
			tableContainer.setPosition(8, devicesLabel.y + devicesLabel.height + 18);
			tableContainer.setHeight(this.height - tableContainer.y - 8);
		}

		configuredDevicesTable.setHeight(tableContainer.height - 16);
		configuredDevicesTable.updateWidgetSizes();

		int tableWidth = this.width() - 16;
		int displayX = 8;
		if(deviceDetail.device() != null) {
			var requiredWidth = configuredDevicesTable.getColumnWidth(0) + configuredDevicesTable.getColumnWidth(1);
			tableWidth = requiredWidth + 48;
			deviceDetail.setVisible(true);
		} else {
			deviceDetail.setVisible(false);
		}

		int detailX = displayX + tableWidth + 5;
		int detailWidth = this.width() - tableWidth - 21;

		deviceDetail.setPosition(detailX, tableContainer.y);
		deviceDetail.setWidth(detailWidth);
		deviceDetail.setHeight(tableContainer.height());
		deviceDetail.updateWidgetSizes();

		tableContainer.setWidth(tableWidth);
		configuredDevicesTable.setWidth(tableContainer.width() - 16);

		newDevicesBar.setWidth(tableWidth - 16);
		if(!hasNewDevices) {
			newDevicesBar.setVisible(false);
			newDevicesLabel.setVisible(false);
		} else {
			newDevicesBar.setVisible(true);
			newDevicesLabel.setVisible(true);
		}
		newDevicesBar.updateWidgetSizes();

	}
}
