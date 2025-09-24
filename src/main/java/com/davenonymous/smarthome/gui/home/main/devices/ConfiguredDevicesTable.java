package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;

import java.util.Comparator;
import java.util.List;

public class ConfiguredDevicesTable extends AbstractDevicesTable {
	HomeCore home;

	public ConfiguredDevicesTable() {
		//noinspection DataFlowIssue
		this.home = HomeScreen.get().selectedHome;

		updateDevices();
		updateWidgetSizes();
	}


	private CellData createHeaderWidget(String text) {
		return createHeaderWidget(text, ContentAlignment.MIDDLE_LEFT);
	}

	private CellData createHeaderWidget(String text, ContentAlignment alignment) {
		WidgetTextBox header = new WidgetTextBox(text);
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
		this.add(0, 0, new Spacer(8, 1));
		this.add(1, 0, createHeaderWidget("Device Name"));
		this.add(2, 0, createHeaderWidget("Zone"));
		this.add(3, 0, createHeaderWidget("Position", ContentAlignment.MIDDLE_CENTER));
		this.add(4, 0, createHeaderWidget("Type"));
		this.add(5, 0, createHeaderWidget("Sensors"));
		this.add(6, 0, new Spacer(1, 25));
		return this;
	}

	public void updateDevices() {
		List<Pair<HomeZone, ConfiguredDevice>> allDevices = home.getAllConfiguredDevices().entrySet().stream()
			.flatMap(entry -> entry.getValue().stream().map(device -> Pair.of(entry.getKey(), device)))
			.sorted(Comparator.comparing(pair -> I18n.get(pair.getSecond().deviceId()), Comparator.naturalOrder()))
			.toList();

		createHeaderRow();
		for(var entry : allDevices) {
			HomeZone zone = entry.getFirst();
			ConfiguredDevice device = entry.getSecond();

			int row = this.getRowCount();
			this.add(1, row, createCellWidget(device.deviceId()));
			this.add(2, row, createCellWidget(zone.name()));
			this.add(3, row, createCellWidget(device.pos().toShortString(), ContentAlignment.MIDDLE_CENTER));

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

			this.add(4, row, cellWidget);
		}
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}
}
