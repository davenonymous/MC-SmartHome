package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record ConfiguredDevice(BlockPos pos, String deviceId, ResourceLocation blockId) {

	public boolean matches(Block block) {
		var givenId = block.builtInRegistryHolder().getKey().location();
		return this.blockId.equals(givenId);
	}

	public boolean matches(BlockState state) {
		return matches(state.getBlock());
	}


	public static final MapCodec<ConfiguredDevice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPos.CODEC.fieldOf("pos").forGetter(ConfiguredDevice::pos),
		Codec.STRING.fieldOf("device").forGetter(ConfiguredDevice::deviceId),
		ResourceLocation.CODEC.fieldOf("block").forGetter(ConfiguredDevice::blockId)
	).apply(instance, ConfiguredDevice::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConfiguredDevice> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, ConfiguredDevice::pos,
		ByteBufCodecs.STRING_UTF8, ConfiguredDevice::deviceId,
		ResourceLocation.STREAM_CODEC, ConfiguredDevice::blockId,
		ConfiguredDevice::new
	);
}
