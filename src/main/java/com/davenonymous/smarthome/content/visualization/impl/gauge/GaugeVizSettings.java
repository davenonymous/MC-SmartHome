package com.davenonymous.smarthome.content.visualization.impl.gauge;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.content.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.content.visualization.SmartHomeVisualizationSettings;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationSettingsCodec;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationSettingsId;
import com.davenonymous.smarthome.content.visualization.annotations.VisualizationSettingsStreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SmartHomeVisualizationSettings
public record GaugeVizSettings(double min, double max, Map<Double, Integer> colorThresholds) implements IVisualizationSettings {
	public static final Map<Double, Integer> DEFAULT_THRESHOLDS = Map.of(0d, ColorHelper.COLOR_GREEN, 50d, ColorHelper.COLOR_ORANGE, 80d, ColorHelper.COLOR_ERRORED.getRGB());

	@VisualizationSettingsId
	public static final ResourceLocation ID = SmartHome.resource("visualization_settings/gauge");

	@VisualizationSettingsCodec
	public static final MapCodec<GaugeVizSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		Codec.DOUBLE.optionalFieldOf("min", 0.0).forGetter(GaugeVizSettings::min),
		Codec.DOUBLE.optionalFieldOf("max", 100.0).forGetter(GaugeVizSettings::max),
		Codec.unboundedMap(Codec.DOUBLE, Codec.INT).optionalFieldOf("colorThresholds", DEFAULT_THRESHOLDS).forGetter(GaugeVizSettings::colorThresholds)
	).apply(inst, GaugeVizSettings::new));

	@VisualizationSettingsStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, GaugeVizSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, GaugeVizSettings::min,
		ByteBufCodecs.DOUBLE, GaugeVizSettings::max,
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.DOUBLE, ByteBufCodecs.INT), GaugeVizSettings::colorThresholds,
		GaugeVizSettings::new
	);

	public GaugeVizSettings(int min, int max) {
		this((double)min, (double)max, DEFAULT_THRESHOLDS);
	}

	@Override
	public List<Widget> createSettingsWidgets(HomeSensor<?, ?> sensor, List<UUID> devices) {
		return List.of();
	}
}
