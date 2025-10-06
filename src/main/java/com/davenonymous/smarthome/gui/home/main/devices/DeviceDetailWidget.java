package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.events.SensorDataUpdatedEvent;
import com.davenonymous.smarthome.gui.events.VisualizationDataUpdatedEvent;
import com.davenonymous.smarthome.gui.general.ScissorScrollWrap;
import com.davenonymous.smarthome.gui.general.WidgetFlowBox;
import com.davenonymous.smarthome.gui.home.main.devices.sensor.SensorBox;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.networking.actions.devices.SetDeviceNamePayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeviceDetailWidget extends WidgetVBox {
	private HomeZone zone;
	private ConfiguredDevice device;

	private StringInputWidget deviceRenameInput;
	private WidgetFlowBox sensorsList;
	private ScissorScrollWrap scrollPanel;

	public DeviceDetailWidget() {
		this.setPaddingHorizontal(6);
		this.setPaddingVertical(4);
		this.setSpacing(10);

		deviceRenameInput = new StringInputWidget("", "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		deviceRenameInput.setHeight(12);
		deviceRenameInput.setDrawBackground(false);
		deviceRenameInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		deviceRenameInput.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				var payload = new SetDeviceNamePayload(zone().home().id(), zone().id(), device(), deviceRenameInput.getValue());
				PacketDistributor.sendToServer(payload);
				updateWidgetSizes();
				return WidgetEventResult.HANDLED;
			});
		deviceRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(NewDeviceEntryWidget.CLICK_TO_RENAME.get()));
		this.addContentBox(deviceRenameInput, FlexAlign.CENTER);

		sensorsList = new WidgetFlowBox();
		sensorsList.setPadding(0);
		sensorsList.setSpacing(2);

		scrollPanel = new ScissorScrollWrap(sensorsList);
		this.addContentBox(scrollPanel, FlexAlign.START);

		this.addListener(SensorDataUpdatedEvent.class, (event, widget) -> {
			if(device == null || !device.id().equals(event.deviceId())) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			updateSensorList();
			return WidgetEventResult.HANDLED;
		});

		this.addListener(VisualizationDataUpdatedEvent.class, (event, widget) -> {
			if(device == null || !device.id().equals(event.deviceId())) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			updateSensorList();
			return WidgetEventResult.HANDLED;
		});

		updateWidgetSizes();
	}

	public ConfiguredDevice device() {
		return device;
	}

	public HomeZone zone() {
		return zone;
	}

	public DeviceDetailWidget setDevice(HomeZone zone, ConfiguredDevice device) {
		this.zone = zone;
		this.device = device;
		if(device == null) {
			deviceRenameInput.setValue("");
		} else {
			deviceRenameInput.setValue(device.name());
			deviceRenameInput.autoWidth();
			deviceRenameInput.nativeWidget().scrollTo(0);
			deviceRenameInput.nativeWidget().scrollTo(device.name().length() / 2);
		}
		updateSensorList();
		updateWidgetSizes();
		return this;
	}

	private void updateSensorList() {
		if(device == null) {
			return;
		}
		if(this.width <= this.paddingHorizontal*2) {
			return;
		}

		sensorsList.clear();
		sensorsList.setWidth(this.width - this.paddingHorizontal*2);
		scrollPanel.setWidth(this.width - this.paddingHorizontal*2);
		scrollPanel.setHeight(this.height - deviceRenameInput.height - this.paddingVertical*2 - this.spacing);
		for(var sensorEntry : device.sensors().entrySet()) {
			var sensorId = sensorEntry.getKey();
			var sensor = ModSensors.getById(sensorId);
			if(sensor == null) {
				continue;
			}

			var box = new SensorBox(zone, device, sensor);
			sensorsList.add(box);
		}
		sensorsList.updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		deviceRenameInput.autoWidth();
		deviceRenameInput.setHeight(12);
		this.update(null);

		if(this.width <= this.paddingHorizontal*2) {
			return;
		}
		sensorsList.setWidth(this.width - this.paddingHorizontal*2);
		sensorsList.setHeight(this.height - deviceRenameInput.height - this.paddingVertical*2 - this.spacing);
		sensorsList.updateWidgetSizes();
		scrollPanel.updateWidgetSizes();
		//sensorsList.spreadBoxes();

	}


	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(device == null) {
			return;
		}

		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}
}
