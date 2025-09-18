package com.davenonymous.smarthome.lib;

import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public record DimPos(ResourceLocation dim, BlockPos pos) {
	public DimPos(Level level, BlockPos pos) {
		this(level.dimension().location(), pos);
	}

	public static final StreamCodec<RegistryFriendlyByteBuf, DimPos> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, DimPos::dim,
		BlockPos.STREAM_CODEC, DimPos::pos,
		DimPos::new
	);

	public static final MapCodec<DimPos> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("dim").forGetter(DimPos::dim),
		BlockPos.CODEC.fieldOf("pos").forGetter(DimPos::pos)
	).apply(instance, DimPos::new));
}
