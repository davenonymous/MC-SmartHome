package com.davenonymous.smarthome.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public record RangerFinderDataComponent(BlockPos A, BlockPos B) {
	public RangerFinderDataComponent(Player player) {
		this(player.blockPosition(), player.blockPosition().above());
	}

	public RangerFinderDataComponent withA(BlockPos newA) {
		return new RangerFinderDataComponent(newA, this.B);
	}

	public RangerFinderDataComponent withB(BlockPos newB) {
		return new RangerFinderDataComponent(this.A, newB);
	}

	public static final MapCodec<RangerFinderDataComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		BlockPos.CODEC.fieldOf("A").forGetter(RangerFinderDataComponent::A),
		BlockPos.CODEC.fieldOf("B").forGetter(RangerFinderDataComponent::B)
	).apply(instance, RangerFinderDataComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, RangerFinderDataComponent> STREAM_CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, RangerFinderDataComponent::A,
		BlockPos.STREAM_CODEC, RangerFinderDataComponent::B,
		RangerFinderDataComponent::new
	);
}
