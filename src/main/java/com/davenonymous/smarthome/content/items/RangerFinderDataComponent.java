package com.davenonymous.smarthome.content.items;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

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

	public String sizeText() {
		return String.format(
			"%dx%dx%d",
			Math.abs(this.A.getX() - this.B.getX()) + 1,
			Math.abs(this.A.getY() - this.B.getY()) + 1,
			Math.abs(this.A.getZ() - this.B.getZ()) + 1
		);
	}

	public AABB toAABB() {
		if(this.A == null || this.B == null) {
			return null;
		}
		double cornerAX = A.getX();
		double cornerAY = A.getY();
		double cornerAZ = A.getZ();
		double cornerBX = B.getX();
		double cornerBY = B.getY();
		double cornerBZ = B.getZ();

		return new AABB(
			Math.min(cornerAX, cornerBX), Math.min(cornerAY, cornerBY), Math.min(cornerAZ, cornerBZ),
			Math.max(cornerAX, cornerBX) + 1, Math.max(cornerAY, cornerBY) + 1, Math.max(cornerAZ, cornerBZ) + 1
		);
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
