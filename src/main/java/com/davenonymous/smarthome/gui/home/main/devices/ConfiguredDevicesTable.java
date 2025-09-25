package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.DeviceSelectionEvent;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfiguredDevicesTable extends AbstractDevicesTable {
	HomeCore home;
	Map<Integer, Pair<HomeZone, ConfiguredDevice>> devices = new HashMap<>();

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
		this.add(0, 0, createHeaderWidget("Device Name"));
		this.add(1, 0, createHeaderWidget("Zone"));
		this.add(2, 0, createHeaderWidget("Position", ContentAlignment.MIDDLE_CENTER));
		this.add(3, 0, createHeaderWidget("Type"));
		this.add(4, 0, createHeaderWidget("Sensors"));
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
		}
	}

	public void updateDevices() {
		this.clear();
		devices.clear();

		this.home = HomeScreen.get().selectedHome;
		List<Pair<HomeZone, ConfiguredDevice>> allDevices = home.getAllConfiguredDevices().entrySet().stream()
			.flatMap(entry -> entry.getValue().stream().map(device -> Pair.of(entry.getKey(), device)))
			.sorted(Comparator.comparing(pair -> I18n.get(pair.getSecond().deviceId()), Comparator.naturalOrder()))
			.toList();

		createHeaderRow();
		for(var entry : allDevices) {
			HomeZone zone = entry.getFirst();
			ConfiguredDevice device = entry.getSecond();

			int row = this.getRowCount();
			devices.put(row, entry);

			this.add(0, row, createCellWidget(device.deviceId()));
			this.add(1, row, createCellWidget(zone.name()));
			this.add(2, row, createCellWidget(device.pos().toShortString(), ContentAlignment.MIDDLE_CENTER));

			CellData cellWidget;
			var deviceBlockState = HomeScreen.get().homeWorldInfo.blockStates().get(device.pos());
			if(deviceBlockState == null) {
				var sprite = new WidgetSprite(HackerNoon.Solid.exclaimation);
				sprite.setColor(ColorHelper.COLOR_ERRORED.getRGB());
				sprite.setSize(sprite.width()/2, sprite.height()/2);
				sprite.setTooltipElements(
					WrappedStringTooltipComponent.red(I18n.get("smarthome.gui.home.devices.table.tooltip.missing_device"))
				);
				cellWidget = new CellData(sprite, ContentAlignment.MIDDLE_CENTER);
			} else {
				cellWidget = createCellWidget(I18n.get(deviceBlockState.getBlock().getDescriptionId()));
			}

			this.add(3, row, cellWidget);
		}
	}
}
