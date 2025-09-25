package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public record ConfiguredDevice(UUID id, BlockPos pos, String name, ResourceLocation blockId, boolean enabled) {

	public ConfiguredDevice(BlockPos pos, String deviceId, ResourceLocation blockId, boolean enabled) {
		this(UUID.randomUUID(), pos, deviceId, blockId, enabled);
	}

	public boolean matches(Block block) {
		var givenId = block.builtInRegistryHolder().getKey().location();
		return this.blockId.equals(givenId);
	}

	public boolean matches(BlockState state) {
		return matches(state.getBlock());
	}

	public static final MapCodec<ConfiguredDevice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.CODEC.fieldOf("id").forGetter(ConfiguredDevice::id),
		BlockPos.CODEC.fieldOf("pos").forGetter(ConfiguredDevice::pos),
		Codec.STRING.fieldOf("device").forGetter(ConfiguredDevice::name),
		ResourceLocation.CODEC.fieldOf("block").forGetter(ConfiguredDevice::blockId),
		Codec.BOOL.optionalFieldOf("enabled", false).forGetter(ConfiguredDevice::enabled)
	).apply(instance, ConfiguredDevice::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConfiguredDevice> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, ConfiguredDevice::id,
		BlockPos.STREAM_CODEC, ConfiguredDevice::pos,
		ByteBufCodecs.STRING_UTF8, ConfiguredDevice::name,
		ResourceLocation.STREAM_CODEC, ConfiguredDevice::blockId,
		ByteBufCodecs.BOOL, ConfiguredDevice::enabled,
		ConfiguredDevice::new
	);

	public ConfiguredDevice withName(String newName) {
		return new ConfiguredDevice(this.id, this.pos, newName, this.blockId, this.enabled);
	}
}
