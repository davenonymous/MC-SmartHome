package com.davenonymous.smarthome.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record IgnoredDevice(BlockPos pos, BlockState state) {
	public boolean matches(Block block) {
		return this.state.getBlock().equals(block);
	}

	public boolean matches(BlockState state) {
		return matches(state.getBlock());
	}

	public static final MapCodec<IgnoredDevice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(IgnoredDevice::pos),
		BlockState.CODEC.fieldOf("state").forGetter(IgnoredDevice::state)
	).apply(instance, IgnoredDevice::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, IgnoredDevice> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, IgnoredDevice::pos,
		ByteBufCodecs.fromCodec(BlockState.CODEC), IgnoredDevice::state,
		IgnoredDevice::new
	);
}
