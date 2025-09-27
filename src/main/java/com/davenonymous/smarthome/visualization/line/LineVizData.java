package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// tick -> value
public record LineVizData(List<Map<Long, Double>> values) implements IVisualizationData {
	private static final StreamCodec<RegistryFriendlyByteBuf, Map<Long, Double>> MAP_STREAM_CODEC =
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.VAR_LONG, ByteBufCodecs.DOUBLE);

	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizData> STREAM_CODEC = StreamCodec.composite(
		MAP_STREAM_CODEC.apply(ByteBufCodecs.list()), LineVizData::values,
		LineVizData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationData> streamCodec() {
		return STREAM_CODEC;
	}
}
