package com.davenonymous.smarthome.content.items;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record ServerDataComponent(UUID owner, UUID id, String name) {

	public ServerDataComponent(Player player) {
		this(player.getUUID(), UUID.randomUUID(), "");
	}

	public ServerDataComponent withName(String newName) {
		return new ServerDataComponent(this.owner, this.id, newName);
	}

	public static final MapCodec<ServerDataComponent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.STRING_CODEC.fieldOf("owner").forGetter(ServerDataComponent::owner),
		UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(ServerDataComponent::id),
		Codec.STRING.fieldOf("name").forGetter(ServerDataComponent::name)
	).apply(instance, ServerDataComponent::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ServerDataComponent> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, ServerDataComponent::owner,
		UUIDUtil.STREAM_CODEC, ServerDataComponent::id,
		ByteBufCodecs.STRING_UTF8, ServerDataComponent::name,
		ServerDataComponent::new
	);
}
