package com.davenonymous.smarthome.gui.home.main.devices.sensor;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.Animations;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.api.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.networking.actions.devices.SetSensorStatePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.PacketDistributor;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SensorBox extends WidgetVBox {
	WidgetHBox header;
	Widget sensorWidget;

	public SensorBox(HomeZone zone, ConfiguredDevice device, HomeSensor<?, ?> sensor) {
		super();
		this.setPadding(4);
		this.setSize(400, 400);
		var name = sensor.getDisplayName().get();
		var description = sensor.getDescription().get();

		var headerBox = new WidgetHBox();
		headerBox.setSize(400, 400);
		headerBox.setPadding(0);
		headerBox.setSpacing(4);
		this.addContentBox(headerBox, FlexAlign.START);

		var label = new WidgetTextBox(name);
		label.setFont(ModFonts.NOKIA);
		label.autoWidth();
		label.autoHeight();
		label.setTextColor(0xFFFFFFFF);
		label.setTooltipElements(WrappedStringTooltipComponent.orange(description));
		headerBox.addContentBox(label, FlexAlign.START);

		headerBox.addFlexBox(new Spacer(1, 8), FlexAlign.START, 1);
		Optional<SensorSettings> optSettings = device.getSettings(sensor.id());
		if(optSettings.isPresent()) {
			SensorSettings settings = optSettings.get();
			var toggle = new WidgetToggle(settings.enabled());
			toggle.addListener(
				ValueChangedEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(new SetSensorStatePayload(zone.home().id(), zone.id(), device, sensor.id(), toggle.getValue()));
					return WidgetEventResult.CONTINUE_PROCESSING;
				});
			headerBox.addContentBox(toggle, FlexAlign.END);
		}
		headerBox.adjustSizeToContent();

		var vizCache = HomeScreen.get().visualizationDataCache;
		var dataCache = HomeScreen.get().sensorDataCache.get(device.id());

		if(dataCache != null && dataCache.containsKey(sensor.id())) {
			var sensorData = dataCache.get(sensor.id());

		}

		boolean hasPlacedViz = false;
		if(vizCache.contains(device.id(), sensor.id())) {
			Map<ResourceLocation, LinkedHashMap<Pair<Instant, Long>, ISensorData>>availableVisualizations = vizCache.get(device.id(), sensor.id());
			if(availableVisualizations != null && availableVisualizations.containsKey(sensor.getDefaultVisualization())) {
				LinkedHashMap<Pair<Instant, Long>, ISensorData> data = availableVisualizations.get(sensor.getDefaultVisualization());
				//noinspection rawtypes
				IVisualization vizImpl = ModVisualizations.getById(sensor.getDefaultVisualization());
				if(vizImpl != null) {
					//noinspection unchecked
					sensorWidget = vizImpl.getWidget(Map.of(device.id(), data), sensor, sensor.getDefaultVisualizationSettings(), new Vec2(120, 70));
					if(sensorWidget != null) {
						this.addContentBox(sensorWidget, FlexAlign.CENTER);
						hasPlacedViz = true;
					}
				}

			}
		}

		if(!hasPlacedViz && dataCache != null) {
			if(dataCache.containsKey(sensor.id())) {
				var sensorData = dataCache.get(sensor.id());
				var value = new WidgetTextBox(sensorData.displayString());
				value.autoWidth();
				value.autoHeight();
				value.setTextColor(0xFFFFFFAA);
				this.sensorWidget = value;
				this.addContentBox(value, FlexAlign.CENTER);
				hasPlacedViz = true;
			}
		}

		if(!hasPlacedViz) {
			var noData = new WidgetSprite(HackerNoon.Regular.spinner);
			noData.addAnimation(Animations.spin(true, 2.0f));
			this.sensorWidget = noData;
			this.setHeight(70);
			this.addFlexBox(new Spacer(1,1), 4);
			this.addContentBox(noData, FlexAlign.CENTER);
			this.addFlexBox(new Spacer(1,1), 5);
		}

		this.update(null);
		this.adjustSizeToContent();

		this.setWidth(Math.max(130, Math.max(this.sensorWidget.width(), label.width() + 20) + 10));
		this.setHeight(Math.max(100, this.height()));
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
