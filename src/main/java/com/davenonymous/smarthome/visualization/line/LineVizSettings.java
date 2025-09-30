package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record LineVizSettings(List<LineVizSeriesSettings> series) implements IVisualizationSettings {

	public static final MapCodec<LineVizSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		LineVizSeriesSettings.CODEC.codec().listOf().fieldOf("seriesColors").forGetter(LineVizSettings::series)
	).apply(inst, LineVizSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizSettings> STREAM_CODEC = StreamCodec.composite(
		LineVizSeriesSettings.STREAM_CODEC.apply(ByteBufCodecs.list()), LineVizSettings::series,
		LineVizSettings::new
	);

	@Override
	public MapCodec<? extends IVisualizationSettings> type() {
		return CODEC;
	}

	@Override
	public StreamCodec<RegistryFriendlyByteBuf, ? extends IVisualizationSettings> streamCodec() {
		return STREAM_CODEC;
	}
}
