package com.davenonymous.smarthome.data;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.UUID;

public record FoundDevice(BlockPos pos, BlockState state, List<ResourceLocation> sensorIds) {
	public static final MapCodec<FoundDevice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(FoundDevice::pos),
		BlockState.CODEC.fieldOf("state").forGetter(FoundDevice::state),
		ResourceLocation.CODEC.listOf().fieldOf("sensors").forGetter(FoundDevice::sensorIds)
	).apply(instance, FoundDevice::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, FoundDevice> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, FoundDevice::pos,
		ByteBufCodecs.fromCodec(BlockState.CODEC), FoundDevice::state,
		ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), FoundDevice::sensorIds,
		FoundDevice::new
	);
}
