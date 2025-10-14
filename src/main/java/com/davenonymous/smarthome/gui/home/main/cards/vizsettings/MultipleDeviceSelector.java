package com.davenonymous.smarthome.gui.home.main.cards.vizsettings;

import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.gui.general.VerticalSelectorPopupWidget;
import com.davenonymous.smarthome.gui.home.main.cards.CardEditorWidget;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;

import java.util.*;

public class MultipleDeviceSelector extends WidgetPanel {
	Map<UUID, ConfiguredDevice> selectedDevices;

	List<Widget> deviceChoices;
	WidgetTextBox deviceLabel;
	VerticalSelectorPopupWidget selector;

	public MultipleDeviceSelector(List<UUID> selectedDevices, HomeSensor<?, ?> sensor) {
		super();
		var home = DashboardScreen.get().selectedHome;
		this.selectedDevices = new HashMap<>();
		for(var deviceId : selectedDevices) {
			var device = home.getDevice(deviceId);
			if(device.isEmpty()) {
				continue;
			}

			this.selectedDevices.put(deviceId, device.get().getSecond());
		}

		Map<HomeZone, Map<UUID, ConfiguredDevice>> availableDevices = DashboardScreen.get().selectedHome.getDevicesWithSensor(sensor);
		this.deviceChoices = new ArrayList<>();

		List<HomeZone> sortedZones = new ArrayList<>(availableDevices.keySet());
		sortedZones.sort(Comparator.comparing(HomeZone::name, Comparator.naturalOrder()));

		for(var zone : sortedZones) {
			if(availableDevices.get(zone).isEmpty()) {
				continue;
			}

			var zoneLabel = new WidgetTextBox("[" + zone.name() + "]", 0xFFFFFF00);
			zoneLabel.autoWidth();
			zoneLabel.autoHeight();
			this.deviceChoices.add(zoneLabel);

			Map<UUID, ConfiguredDevice> devicesInZone = new HashMap<>(availableDevices.get(zone));
			List<ConfiguredDevice> devicesInZoneList = new ArrayList<>(devicesInZone.values());
			devicesInZoneList.sort(Comparator.comparing(ConfiguredDevice::name, Comparator.naturalOrder()));
			for(var device : devicesInZoneList) {
				var deviceWidget = new WidgetTextBox(device.name(), 0xFFFFFFFF);
				if(this.selectedDevices.containsKey(device.id())) {
					deviceWidget.setTextColor(ColorHelper.COLOR_ORANGE);
				}
				deviceWidget.addListener(MouseClickEvent.class, (event, widget) -> {
					var oldDevices = this.selectedDevices;
					if(this.selectedDevices.containsKey(device.id())) {
						this.selectedDevices.remove(device.id());
						deviceWidget.setTextColor(0xFFFFFFFF);
					} else {
						this.selectedDevices.put(device.id(), device);
						deviceWidget.setTextColor(ColorHelper.COLOR_ORANGE);
					}

					this.deviceLabel.setText(selectedDevicesName());
					this.deviceLabel.autoWidth(155);
					this.deviceLabel.autoHeight();
					this.setSize(this.deviceLabel.width()+2, this.deviceLabel.height()+2);

					this.fireEvent(new ValueChangedEvent<>(oldDevices, this.selectedDevices));
					return WidgetEventResult.HANDLED;
				});
				deviceWidget.autoWidth();
				deviceWidget.autoHeight();
				this.deviceChoices.add(deviceWidget);
			}

		}

		this.deviceLabel = new WidgetTextBox(selectedDevicesName(), 0xFFFFFFFF, ColorHelper.COLOR_ORANGE);
		this.deviceLabel.setWordWrap(true);
		this.deviceLabel.autoWidth(155);
		this.deviceLabel.autoHeight();
		this.deviceLabel.addListener(
			MouseClickEvent.class, (event, widget) -> {
				CardEditorWidget parent = this.getParentByType(CardEditorWidget.class);
				if(parent == null) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				selector = VerticalSelectorPopupWidget.openAt(parent.getMouseX(), parent.getMouseY(), ContentAlignment.TOP_LEFT, deviceChoices.toArray(new Widget[0]));
				selector.zLevel += 20;
				parent.add(selector);
				return WidgetEventResult.HANDLED;
			});
		this.add(this.deviceLabel);

		this.setSize(this.deviceLabel.width()+2, this.deviceLabel.height()+2);
	}

	private String selectedDevicesName() {
		return this.selectedDevices.values().stream().map(ConfiguredDevice::name).reduce((a, b) -> a + ", " + b).orElse("<none>");
	}

	public Map<UUID, ConfiguredDevice> selectedDevices() {
		return selectedDevices;
	}
}
