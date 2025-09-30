package com.davenonymous.smarthome.gui.home.main.devices.sensor;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public class SensorBox extends WidgetVBox {
	WidgetHBox header;
	Widget sensorWidget;

	public SensorBox(ConfiguredDevice device, HomeSensor<?, ?> sensor) {
		super();
		this.setPadding(4);
		this.setSize(400, 400);
		var name = sensor.getDisplayName().get();
		var description = sensor.getDescription().get();

		var label = new WidgetTextBox(name);
		label.setFont(ModFonts.NOKIA);
		label.autoWidth();
		label.autoHeight();
		label.setTextColor(0xFFFFFFFF);
		label.setTooltipElements(WrappedStringTooltipComponent.orange(description));
		this.addContentBox(label, FlexAlign.START);

		var vizCache = HomeScreen.get().visualizationDataCache;
		var dataCache = HomeScreen.get().sensorDataCache.get(device.id());
		if(vizCache.contains(device.id(), sensor.id())) {
			Map<ResourceLocation, LinkedHashMap<Pair<Instant, Long>, ISensorData>>availableVisualizations = vizCache.get(device.id(), sensor.id());
			if(availableVisualizations != null && availableVisualizations.containsKey(sensor.getDefaultVisualization())) {
				var data = availableVisualizations.get(sensor.getDefaultVisualization());
				//noinspection rawtypes
				IVisualization vizImpl = ModVisualizations.getById(sensor.getDefaultVisualization());
				if(vizImpl != null) {
					//noinspection unchecked
					sensorWidget = vizImpl.getWidget(data, sensor, sensor.getDefaultVisualizationSettings());
					if(sensorWidget != null) {
						this.addContentBox(sensorWidget, FlexAlign.CENTER);
					}
				}
			}
		} else if(dataCache != null) {
			if(dataCache.containsKey(sensor.id())) {
				var sensorData = dataCache.get(sensor.id());
				var value = new WidgetTextBox(sensorData.displayString());
				value.autoWidth();
				value.autoHeight();
				value.setTextColor(0xFFFFFFAA);
				this.sensorWidget = value;
				this.addContentBox(value, FlexAlign.CENTER);
			}
		}

		this.update(null);
		this.adjustSizeToContent();
		if(this.sensorWidget == null) {
			this.setWidth(40);
		} else {
			this.setWidth(this.sensorWidget.width() + 10);
		}
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
