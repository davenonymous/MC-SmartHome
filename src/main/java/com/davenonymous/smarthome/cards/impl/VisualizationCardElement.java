package com.davenonymous.smarthome.cards.impl;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualization;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.cards.SmartHomeCardElement;
import com.davenonymous.smarthome.cards.annotations.*;
import com.davenonymous.smarthome.gui.home.main.cards.vizsettings.MultipleDeviceSelector;
import com.davenonymous.smarthome.gui.home.main.cards.vizsettings.SensorSelector;
import com.davenonymous.smarthome.gui.home.main.cards.vizsettings.VisualizationSelector;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.ClientCache;
import com.davenonymous.smarthome.sensor.energy.EnergyStorage;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.setup.dynamic.ModVisualizations;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.davenonymous.smarthome.visualization.line.LineViz;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SmartHomeCardElement
public record VisualizationCardElement(UUID id, ResourceLocation vizId, ResourceLocation sensorId, List<UUID> devices, IVisualizationSettings vizSettings, Vec2 size) implements HomeCardElement<Widget> {
	@HomeCardElementId
	public static final ResourceLocation ID = SmartHome.resource("card_element/viz");

	@HomeCardElementName
	@I18DataGen(lang = "en_us", string = "Visualization")
	@I18DataGen(lang = "de_de", string = "Visualisierung")
	public static final I18String NAME = I18String.data("card_element.viz", "label");

	@HomeCardElementDefault
	public static VisualizationCardElement createDefault() {
		return new VisualizationCardElement(UUID.randomUUID(), LineViz.ID, EnergyStorage.ID, List.of(), ModVisualizations.getById(LineViz.ID).getDefaultSettings(), new Vec2(120, 70));
	}

	@HomeCardElementIcon
	public static final ResourceLocation ICON = HackerNoon.Regular.chartLine;

	@HomeCardElementCodec
	public static final MapCodec<VisualizationCardElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(VisualizationCardElement::id),
		ResourceLocation.CODEC.fieldOf("viz_id").forGetter(VisualizationCardElement::vizId),
		ResourceLocation.CODEC.fieldOf("sensor_id").forGetter(VisualizationCardElement::sensorId),
		UUIDUtil.STRING_CODEC.listOf().fieldOf("devices").forGetter(VisualizationCardElement::devices),
		IVisualizationSettings.CODEC.fieldOf("viz_settings").forGetter(VisualizationCardElement::vizSettings),
		MoreCodecs.VEC2_CODEC.codec().optionalFieldOf("size", new Vec2(120, 70)).forGetter(VisualizationCardElement::size)
	).apply(instance, VisualizationCardElement::new));

	@HomeCardElementStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, VisualizationCardElement> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, VisualizationCardElement::id,
		ResourceLocation.STREAM_CODEC, VisualizationCardElement::vizId,
		ResourceLocation.STREAM_CODEC, VisualizationCardElement::sensorId,
		UUIDUtil.STREAM_CODEC.apply(ByteBufCodecs.list()), VisualizationCardElement::devices,
		IVisualizationSettings.STREAM_CODEC, VisualizationCardElement::vizSettings,
		MoreCodecs.VEC2_STREAM_CODEC, VisualizationCardElement::size,
		VisualizationCardElement::new
	);

	@Override
	public Widget createWidget() {
		//noinspection rawtypes
		IVisualization viz = ModVisualizations.getById(vizId);

		// TODO: continue here.
		// we need a different way to create the widget from the viz.
		IVisualizationSettings vizSettings = this.vizSettings;
		if(!viz.getDefaultSettings().getClass().equals(vizSettings.getClass())) {
			vizSettings = viz.getDefaultSettings();
		}

		//noinspection unchecked
		return viz.getWidget(
			ClientCache.dataByDevices(devices, sensorId, vizId),
			ModSensors.getById(sensorId),
			vizSettings,
			size
		);
	}

	public VisualizationCardElement withSize(Vec2 newSize) {
		return new VisualizationCardElement(id, vizId, sensorId, devices, vizSettings, newSize);
	}

	private WidgetTextBox createLabel(String text) {
		var wigget = new WidgetTextBox(text);
		wigget.setFont(ModFonts.NOKIA);
		wigget.autoWidth();
		wigget.autoHeight();
		return wigget;
	}

	@Override
	public List<Widget> createSettingWidgets() {
		List<Widget> result = new ArrayList<>();

		result.add(createLabel("Sensor:"));
		var sensor = ModSensors.getById(sensorId);
		result.add(new SensorSelector(sensor));

		result.add(createLabel("Type:"));
		result.add(new VisualizationSelector(ModVisualizations.getById(vizId)));

		result.add(createLabel("Devices:"));
		result.add(new MultipleDeviceSelector(devices, sensor));

		result.addAll(vizSettings.createSettingsWidgets(sensor, devices));

		return result;
	}

	@Override
	public VisualizationCardElement loadSettings(List<Widget> settingsWidgets) {
		var sensorSelector = (SensorSelector)settingsWidgets.get(1);
		var deviceSelector = (MultipleDeviceSelector)settingsWidgets.get(5);
		var vizSelector = (VisualizationSelector)settingsWidgets.get(3);

		var sensor = sensorSelector.selectedSensor();
		var viz = vizSelector.selectedVisualization();
		var newVizSettings = viz.loadSettings(settingsWidgets);

		return new VisualizationCardElement(id, vizSelector.selectedVisualization().getType(), sensorSelector.selectedSensor().id(), deviceSelector.selectedDevices().keySet().stream().toList(), newVizSettings, size);
	}
}
