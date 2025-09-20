package com.davenonymous.smarthome.lib;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public record DimPos(ResourceLocation dim, BlockPos pos) {
	public DimPos(Level level, BlockPos pos) {
		this(level.dimension().location(), pos);
	}

	public ServerLevel getServerLevel(MinecraftServer server) {
		return server.getLevel(ResourceKey.create(Registries.DIMENSION, dim));
	}

	public <T extends BlockEntity> T getBlockEntity(MinecraftServer server, Class<T> blockEntityClass) {
		var level = getServerLevel(server);
		if(level == null) {
			return null;
		}

		var blockEntity = level.getBlockEntity(pos);
		if(blockEntity == null) {
			return null;
		}

		if(!blockEntityClass.isAssignableFrom(blockEntity.getClass())) {
			return null;
		}

		//noinspection unchecked
		return (T)blockEntity;
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
