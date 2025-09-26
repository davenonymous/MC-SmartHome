package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.SensorData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.SensorDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.networking.actions.SetDeviceNamePayload;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class DeviceDetailWidget extends WidgetVBox {
	private HomeZone zone;
	private ConfiguredDevice device;

	private StringInputWidget deviceRenameInput;
	private WidgetVBox sensorsList;

	public DeviceDetailWidget() {
		this.setPadding(8);
		this.setSpacing(4);

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
		deviceRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(I18n.get("smarthome.gui.home.devices.add.renameable")));
		this.addContentBox(deviceRenameInput, FlexAlign.CENTER);

		sensorsList = new WidgetVBox();
		sensorsList.setSpacing(2);
		this.addContentBox(sensorsList, FlexAlign.FILL);

		this.addListener(SensorDataUpdatedEvent.class, (event, widget) -> {
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
		sensorsList.setWidth(this.width - this.padding*2);
		updateSensorList();
		updateWidgetSizes();
		return this;
	}

	private void updateSensorList() {
		sensorsList.clear();
		if(device == null) {
			return;
		}

		List<SensorData> dataCache = HomeScreen.get().sensorDataCache.get(device.id());
		for(var sensorSettings : device.sensors()) {
			var sensor = ModSensors.getBySettings(sensorSettings);
			if(sensor == null) {
				continue;
			}

			var name = I18n.get(sensor.nameTranslationKey());
			var description = I18n.get(sensor.descriptionTranslationKey());

			var label = new WidgetTextBox(name);
			label.autoWidth();
			label.autoHeight();
			label.setTextColor(0xFFFFFFFF);
			label.setTooltipElements(WrappedStringTooltipComponent.orange(description));
			sensorsList.addContentBox(label, FlexAlign.FILL);

			if(dataCache != null) {
				Optional<SensorData> optSensorData = dataCache.stream().filter(data -> ModSensors.getByData(data) == sensor).findFirst();
				if(optSensorData.isEmpty()) {
					continue;
				}
				var sensorData = optSensorData.get();
				var value = new WidgetTextBox(sensorData.displayString());
				value.autoWidth();
				value.autoHeight();
				value.setTextColor(0xFFFFFFAA);
				sensorsList.addContentBox(value, FlexAlign.FILL);
			}
		}
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		deviceRenameInput.autoWidth();
		deviceRenameInput.setHeight(12);
		sensorsList.setWidth(this.width - this.padding*2);
		sensorsList.setHeight(this.height - deviceRenameInput.height - this.padding*2 - this.spacing);

		sensorsList.update(null);
		this.update(null);
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
