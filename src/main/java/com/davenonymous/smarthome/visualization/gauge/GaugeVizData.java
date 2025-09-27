package com.davenonymous.smarthome.visualization.gauge;

import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record GaugeVizData(double value) implements IVisualizationData {
	public static final StreamCodec<RegistryFriendlyByteBuf, GaugeVizData> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, GaugeVizData::value,
		GaugeVizData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationData> streamCodec() {
		return STREAM_CODEC;
	}
}
