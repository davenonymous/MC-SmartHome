package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.SensorDataUpdatedEvent;
import com.davenonymous.smarthome.gui.events.VisualizationDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.networking.actions.SetDeviceNamePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.davenonymous.smarthome.setup.content.ModVisualizations;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.Optional;

public class DeviceDetailWidget extends WidgetVBox {
	private HomeZone zone;
	private ConfiguredDevice device;

	private StringInputWidget deviceRenameInput;
	private WidgetVBox sensorsList;

	public DeviceDetailWidget() {
		this.setPaddingHorizontal(8);
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
		deviceRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(I18n.get("smarthome.gui.home.devices.add.renameable")));
		this.addContentBox(deviceRenameInput, FlexAlign.CENTER);

		sensorsList = new WidgetVBox();
		sensorsList.setSpacing(2);
		this.addContentBox(sensorsList, FlexAlign.START);

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
		sensorsList.clear();
		if(device == null) {
			return;
		}

		var dataCache = HomeScreen.get().sensorDataCache.get(device.id());
		var vizCache = HomeScreen.get().visualizationDataCache;
		for(var sensorEntry : device.sensors().entrySet()) {
			var sensorId = sensorEntry.getKey();
			var sensor = ModSensors.getById(sensorId);
			if(sensor == null) {
				continue;
			}

			var name = I18n.get(sensor.nameTranslationKey());
			var description = I18n.get(sensor.descriptionTranslationKey());

			var label = new WidgetTextBox(name);
			label.setFont(ModFonts.NOKIA);
			label.autoWidth();
			label.autoHeight();
			label.setTextColor(0xFFFFFFFF);
			label.setTooltipElements(WrappedStringTooltipComponent.orange(description));
			sensorsList.addContentBox(label, FlexAlign.START);

			if(sensor.hasDefaultVisualization() && vizCache.contains(device.id(), sensor.id())) {
				Map<ResourceLocation, IVisualizationData> availableVisualizations = vizCache.get(device.id(), sensor.id());
				if(availableVisualizations != null && availableVisualizations.containsKey(sensor.getDefaultVisualization())) {
					var data = availableVisualizations.get(sensor.getDefaultVisualization());
					//noinspection rawtypes
					IVisualization vizImpl = ModVisualizations.getById(sensor.getDefaultVisualization());
					if(vizImpl != null) {
						//noinspection unchecked
						var widget = vizImpl.getWidget(data, sensor.getDefaultVisualizationSettings());
						if(widget != null) {
							sensorsList.addContentBox(widget, FlexAlign.CENTER);
						}
					}
				}
			} else if(dataCache != null) {
				Optional<ISensorData> optSensorData = dataCache.stream().filter(data -> ModSensors.getByData(data) == sensor).findFirst();
				if(optSensorData.isEmpty()) {
					continue;
				}
				var sensorData = optSensorData.get();
				var value = new WidgetTextBox(sensorData.displayString());
				value.autoWidth();
				value.autoHeight();
				value.setTextColor(0xFFFFFFAA);
				sensorsList.addContentBox(value, FlexAlign.CENTER);
			}
			sensorsList.addContentBox(new Spacer(1, 10), FlexAlign.START);
		}
		sensorsList.update(null);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		deviceRenameInput.autoWidth();
		deviceRenameInput.setHeight(12);
		sensorsList.setWidth(this.width - this.paddingHorizontal*2);
		sensorsList.setHeight(this.height - deviceRenameInput.height - this.paddingVertical*2 - this.spacing);

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
