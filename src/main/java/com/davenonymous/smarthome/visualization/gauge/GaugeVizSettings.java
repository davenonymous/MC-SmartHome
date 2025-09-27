package com.davenonymous.smarthome.visualization.gauge;

import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

// TODO: turn this from POC to a real thing, i.e. fix/add more settings
public record GaugeVizSettings(double min, double max, Map<Double, Integer> colorThresholds) implements IVisualizationSettings {
	public static final Map<Double, Integer> DEFAULT_THRESHOLDS = Map.of(0d, ColorHelper.COLOR_GREEN, 50d, ColorHelper.COLOR_ORANGE, 80d, ColorHelper.COLOR_ERRORED.getRGB());

	public static final MapCodec<GaugeVizSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		Codec.DOUBLE.optionalFieldOf("min", 0.0).forGetter(GaugeVizSettings::min),
		Codec.DOUBLE.optionalFieldOf("max", 100.0).forGetter(GaugeVizSettings::max),
		Codec.unboundedMap(Codec.DOUBLE, Codec.INT).optionalFieldOf("colorThresholds", DEFAULT_THRESHOLDS).forGetter(GaugeVizSettings::colorThresholds)
	).apply(inst, GaugeVizSettings::new));

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
	public MapCodec<? extends IVisualizationSettings> type() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationSettings> streamCodec() {
		return STREAM_CODEC;
	}
}
