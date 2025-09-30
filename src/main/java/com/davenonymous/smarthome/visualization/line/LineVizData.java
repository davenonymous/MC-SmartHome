package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;

// tick -> column -> value
public record LineVizData(Map<Long, Map<String, Double>> values) implements IVisualizationData {
	private static final StreamCodec<RegistryFriendlyByteBuf, Map<String, Double>> FIELD_VALUE_STREAM_CODEC =
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.DOUBLE);

	private static final StreamCodec<RegistryFriendlyByteBuf, Map<Long, Map<String, Double>>> TICK_FIELD_STREAM_CODEC =
		ByteBufCodecs.map(HashMap::new, ByteBufCodecs.VAR_LONG, FIELD_VALUE_STREAM_CODEC);

	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizData> STREAM_CODEC = StreamCodec.composite(
		TICK_FIELD_STREAM_CODEC, LineVizData::values,
		LineVizData::new
	);

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationData> streamCodec() {
		return STREAM_CODEC;
	}
}
