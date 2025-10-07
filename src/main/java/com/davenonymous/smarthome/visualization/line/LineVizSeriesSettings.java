package com.davenonymous.smarthome.visualization.line;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LineVizSeriesSettings(boolean enabled, Integer color, String label) {
	public static final MapCodec<LineVizSeriesSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(LineVizSeriesSettings::enabled),
		Codec.INT.fieldOf("color").forGetter(LineVizSeriesSettings::color),
		Codec.STRING.fieldOf("label").forGetter(LineVizSeriesSettings::label)
	).apply(inst, LineVizSeriesSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizSeriesSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, LineVizSeriesSettings::enabled,
		ByteBufCodecs.INT, LineVizSeriesSettings::color,
		ByteBufCodecs.STRING_UTF8, LineVizSeriesSettings::label,
		LineVizSeriesSettings::new
	);
}
