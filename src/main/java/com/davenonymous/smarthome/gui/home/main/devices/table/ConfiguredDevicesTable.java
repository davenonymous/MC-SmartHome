package com.davenonymous.smarthome.gui.home.main.devices.table;

import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.gui.events.DeviceSelectionEvent;
import com.davenonymous.smarthome.gui.general.BlockStateWidget;
import com.davenonymous.smarthome.gui.general.HoverableWidgetTable;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.devices.SetDeviceStatePayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class ConfiguredDevicesTable extends HoverableWidgetTable {
	HomeCore home;
	Map<Integer, Pair<HomeZone, ConfiguredDevice>> devices = new HashMap<>();

	@I18DataGen(lang = "en_us", string = "Device")
	@I18DataGen(lang = "de_de", string = "Gerät")
	public static final I18String HEADER_DEVICE = I18String.gui("home.devices.table", "header.device");

	@I18DataGen(lang = "en_us", string = "Position")
	@I18DataGen(lang = "de_de", string = "Position")
	public static final I18String HEADER_POSITION = I18String.gui("home.devices.table", "header.position");

	@I18DataGen(lang = "en_us", string = "State")
	@I18DataGen(lang = "de_de", string = "Status")
	public static final I18String HEADER_STATE = I18String.gui("home.devices.table", "header.state");

	@I18DataGen(lang = "en_us", string = "Sensors")
	@I18DataGen(lang = "de_de", string = "Sensoren")
	public static final I18String HEADER_SENSORS = I18String.gui("home.devices.table", "header.sensors");

	@I18DataGen(lang = "en_us", string = "The device is missing (block removed?)")
	@I18DataGen(lang = "de_de", string = "Das Gerät fehlt (Block entfernt?)")
	public static final I18String MISSING_DEVICE_TOOLTIP = I18String.gui("home.devices.table", "tooltip.missing_device");

	@I18DataGen(lang = "en_us", string = "-")
	@I18DataGen(lang = "de_de", string = "-")
	public static final I18String NO_SENSORS = I18String.gui("smarthome.sensors", "no_sensors");

	public ConfiguredDevicesTable() {
		super();

		updateDevices();
		updateWidgetSizes();
	}


	private CellData createHeaderWidget(String text) {
		return createHeaderWidget(text, ContentAlignment.MIDDLE_LEFT);
	}

	private CellData createHeaderWidget(String text, ContentAlignment alignment) {
		WidgetTextBox header = new WidgetTextBox(text);
		header.setFont(ModFonts.NOKIA);
		header.autoWidth();
		header.autoHeight();
		header.setTextColor(0xFFFFFFFF);
		return new CellData(header, alignment);
	}

	private CellData createCellWidget(String text) {
		return createCellWidget(text, ContentAlignment.MIDDLE_LEFT);
	}

	private CellData createCellWidget(String text, ContentAlignment alignment) {
		WidgetTextBox cell = new WidgetTextBox(text);
		cell.autoWidth();
		cell.autoHeight();
		cell.setTextColor(0xFFAAAAAA);
		return new CellData(cell, alignment);
	}

	private ConfiguredDevicesTable createHeaderRow() {
		this.clear();
		this.add(0, 0, createHeaderWidget(HEADER_DEVICE.get()));
		this.add(1, 0, new Spacer(5, 5));
		this.add(2, 0, createHeaderWidget(HEADER_POSITION.get()));
		this.add(3, 0, createHeaderWidget(HEADER_STATE.get()));
		this.add(4, 0, createHeaderWidget(HEADER_SENSORS.get()));
		return this;
	}

	@Override
	public void onRowClick(int row, CellData cell) {
		var device = devices.get(row);
		if(device == null) {
			return;
		}

		this.fireEvent(new DeviceSelectionEvent(device.getFirst(), device.getSecond()));
	}

	@Override
	public void onRowHover(int row, CellData cell, boolean isHovered) {
		if(row == 0) {
			return;
		}

		if(cell.widget() instanceof WidgetTextBox textBox) {
			if(isHovered) {
				textBox.setTextColor(0xFFFFFFFF);
			} else {
				textBox.setTextColor(0xFFAAAAAA);
			}
		} else if(cell.widget() instanceof TextCell textCell) {
			if(isHovered) {
				textCell.setTextColor(0xFFFFFFFF);
			} else {
				textCell.setTextColor(0xFFAAAAAA);
			}
		}
	}

	public void updateDevices() {
		this.clear();
		devices.clear();

		this.home = DashboardScreen.get().selectedHome;
		if(this.home == null) {
			return;
		}

		List<Pair<HomeZone, ConfiguredDevice>> allDevices = home.getAllConfiguredDevices().entrySet().stream()
			.flatMap(entry -> entry.getValue().stream().map(device -> Pair.of(entry.getKey(), device)))
			.sorted(Comparator.comparing(pair -> I18n.get(pair.getSecond().name()), Comparator.naturalOrder()))
			.toList();

		createHeaderRow();
		for(var entry : allDevices) {
			HomeZone zone = entry.getFirst();
			ConfiguredDevice device = entry.getSecond();

			int row = this.getRowCount();
			devices.put(row, entry);

			var positionCell = new TextCell(zone.name(), device.pos().toShortString());
			this.add(2, row, new CellData(positionCell, ContentAlignment.MIDDLE_LEFT, true));

			var deviceBlockState = DashboardScreen.get().getMenu().homeWorldInfo.blockStates().get(device.pos());
			if(deviceBlockState == null) {
				var sprite = new WidgetSprite(HackerNoon.Solid.exclamation);
				sprite.setColor(ColorHelper.COLOR_ERRORED.getRGB());
				sprite.setSize(sprite.width()/2, sprite.height()/2);
				sprite.setTooltipElements(
					WrappedStringTooltipComponent.red(MISSING_DEVICE_TOOLTIP.get())
				);

				var deviceNameCell = new TextCell(device.name());
				this.add(1, row, new CellData(deviceNameCell, ContentAlignment.MIDDLE_LEFT, true));
				this.add(0, row, new CellData(sprite, ContentAlignment.MIDDLE_CENTER));
			} else {
				var blockName = I18n.get(deviceBlockState.getBlock().getDescriptionId());

				var deviceNameCell = new TextCell(blockName, device.name().equals(blockName) ? "" : device.name());
				this.add(1, row, new CellData(deviceNameCell, ContentAlignment.MIDDLE_LEFT, true));

				var blockStateWidget = new BlockStateWidget(deviceBlockState);
				blockStateWidget.setSize(24, 24);
				blockStateWidget.setTooltipElements(WrappedStringTooltipComponent.orange(blockName));

				this.add(0, row, blockStateWidget);
			}

			var statusToggle = new WidgetToggle(device.enabled());
			statusToggle.addListener(ValueChangedEvent.class, (event, widget) -> {
				PacketDistributor.sendToServer(new SetDeviceStatePayload(zone.home().id(), zone.id(), device, statusToggle.getValue()));
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
			this.add(3, row, statusToggle);

			Set<String> sensorNames = new HashSet<>();
			for(var sensorEntry : device.sensors().entrySet()) {
				var sensorId = sensorEntry.getKey();
				var sensorSettings = sensorEntry.getValue();
				var sensor = ModSensors.getById(sensorId);
				if(sensor == null) {
					continue;
				}
				if(sensor.isGeneric()) {
					continue;
				}
				sensorNames.add(sensor.getDisplayName().get());
			}
			String sensorsText = String.join(", ", sensorNames);
			if(sensorsText.isEmpty()) {
				sensorsText = NO_SENSORS.get();
			}
			this.add(4, row, createCellWidget(sensorsText));
		}
	}
}
