package com.davenonymous.smarthome.visualization.line;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.api.visualization.SmartHomeVisualizationSettings;
import com.davenonymous.smarthome.visualization.annotations.VisualizationSettingsCodec;
import com.davenonymous.smarthome.visualization.annotations.VisualizationSettingsId;
import com.davenonymous.smarthome.visualization.annotations.VisualizationSettingsStreamCodec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@SmartHomeVisualizationSettings
public record LineVizSettings(List<LineVizSeriesSettings> series) implements IVisualizationSettings {
	@VisualizationSettingsId
	public static final ResourceLocation ID = SmartHome.resource("visualization_settings/line");

	@VisualizationSettingsCodec
	public static final MapCodec<LineVizSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		LineVizSeriesSettings.CODEC.codec().listOf().fieldOf("seriesColors").forGetter(LineVizSettings::series)
	).apply(inst, LineVizSettings::new));

	@VisualizationSettingsStreamCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizSettings> STREAM_CODEC = StreamCodec.composite(
		LineVizSeriesSettings.STREAM_CODEC.apply(ByteBufCodecs.list()), LineVizSettings::series,
		LineVizSettings::new
	);
}
