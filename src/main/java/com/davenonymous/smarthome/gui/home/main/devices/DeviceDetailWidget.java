package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.events.SensorDataUpdatedEvent;
import com.davenonymous.smarthome.gui.events.VisualizationDataUpdatedEvent;
import com.davenonymous.smarthome.gui.general.ScissorScrollWrap;
import com.davenonymous.smarthome.gui.general.WidgetFlowBox;
import com.davenonymous.smarthome.gui.home.main.devices.sensor.SensorBox;
import com.davenonymous.smarthome.gui.home.main.devices.sensor.SensorTable;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.devices.AddDevicePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeviceDetailWidget extends WidgetVBox {
	@I18DataGen(lang = "en_us", string = "Sensors")
	@I18DataGen(lang = "de_de", string = "Sensoren")
	public static final I18String SENSOR_STATES_LABEL = I18String.gui("devices.detail", "sensor_states");

	private HomeZone zone;
	private ConfiguredDevice device;

	private StringInputWidget deviceRenameInput;
	private WidgetFlowBox sensorsBoxList;
	private SensorTable sensorStateTable;
	private ScissorScrollWrap scrollPanel;

	public DeviceDetailWidget() {
		this.setPaddingHorizontal(6);
		this.setPaddingVertical(2);
		this.setSpacing(10);

		deviceRenameInput = new StringInputWidget("", ModFonts.SAFE_FONT_CHARS);
		deviceRenameInput.setHeight(12);
		deviceRenameInput.setDrawBackground(false);
		deviceRenameInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		deviceRenameInput.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				var payload = new AddDevicePayload(zone(), device().withName(deviceRenameInput.getValue()));
				PacketDistributor.sendToServer(payload);
				updateWidgetSizes();
				return WidgetEventResult.HANDLED;
			});
		deviceRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(NewDeviceEntryWidget.CLICK_TO_RENAME.get()));
		this.addContentBox(deviceRenameInput, FlexAlign.CENTER);

		WidgetTextBox sensorStateLabel = new WidgetTextBox(SENSOR_STATES_LABEL.get());
		sensorStateLabel.setFont(ModFonts.PIXEL);
		sensorStateLabel.autoWidth();
		sensorStateLabel.autoHeight();
		sensorStateLabel.setTextColor(0xFFFFFFFF);
		this.addContentBox(sensorStateLabel, FlexAlign.START);


		sensorStateTable = new SensorTable();
		sensorStateTable.setSize(this.width - this.paddingHorizontal*2, 50);
		this.addContentBox(sensorStateTable, FlexAlign.START);

		sensorsBoxList = new WidgetFlowBox();
		sensorsBoxList.setPadding(0);
		sensorsBoxList.setSpacing(2);

		scrollPanel = new ScissorScrollWrap(sensorsBoxList);
		this.addContentBox(scrollPanel, FlexAlign.START);

		this.addListener(SensorDataUpdatedEvent.class, (event, widget) -> {
			if(device == null || !device.id().equals(event.deviceId())) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			sensorStateTable.populate(zone, device);
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

		sensorStateTable.populate(zone, device);
		sensorStateTable.adjustSizeToContent(false);
		sensorStateTable.setWidth(this.width - this.paddingHorizontal*2);
		sensorStateTable.setHeight(sensorStateTable.height() + 18);
		sensorsBoxList.clear();
		sensorsBoxList.setWidth(this.width - this.paddingHorizontal*2);
		scrollPanel.setWidth(this.width - this.paddingHorizontal*2);
		scrollPanel.setHeight(this.height - deviceRenameInput.height - this.paddingVertical*2 - this.spacing);
		for(var sensorEntry : device.sensors().entrySet()) {
			var sensorId = sensorEntry.getKey();
			var sensorSettings = sensorEntry.getValue();
			if(!sensorSettings.enabled()) {
				continue;
			}

			var sensor = ModSensors.getById(sensorId);
			if(sensor == null) {
				continue;
			}

			var box = new SensorBox(zone, device, sensor);
			sensorsBoxList.add(box);
		}
		sensorsBoxList.updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		deviceRenameInput.autoWidth();
		deviceRenameInput.setHeight(12);


		if(this.width <= this.paddingHorizontal*2) {
			return;
		}
		sensorStateTable.setWidth(this.width - this.paddingHorizontal*2);
		sensorStateTable.updateWidgetSizes();

		sensorsBoxList.setWidth(this.width - this.paddingHorizontal*2);
		sensorsBoxList.setHeight(this.height - deviceRenameInput.height - this.paddingVertical*2 - this.spacing);
		sensorsBoxList.updateWidgetSizes();
		scrollPanel.updateWidgetSizes();
		//sensorsList.spreadBoxes();

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
