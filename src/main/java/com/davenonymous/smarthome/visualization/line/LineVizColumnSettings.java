package com.davenonymous.smarthome.visualization.line;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record LineVizColumnSettings(boolean enabled, Integer color, String label) {
	public static final MapCodec<LineVizColumnSettings> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
		Codec.BOOL.optionalFieldOf("enabled", true).forGetter(LineVizColumnSettings::enabled),
		Codec.INT.fieldOf("color").forGetter(LineVizColumnSettings::color),
		Codec.STRING.fieldOf("label").forGetter(LineVizColumnSettings::label)
	).apply(inst, LineVizColumnSettings::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, LineVizColumnSettings> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BOOL, LineVizColumnSettings::enabled,
		ByteBufCodecs.INT, LineVizColumnSettings::color,
		ByteBufCodecs.STRING_UTF8, LineVizColumnSettings::label,
		LineVizColumnSettings::new
	);
}
