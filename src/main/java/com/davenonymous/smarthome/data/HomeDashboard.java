package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record HomeDashboard(UUID id, String label, ResourceLocation icon, Map<Vec2, UUID> cards) {

	public static final MapCodec<HomeDashboard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("id").forGetter(HomeDashboard::id),
			Codec.STRING.fieldOf("label").forGetter(HomeDashboard::label),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(HomeDashboard::icon),
			Codec.unboundedMap(MoreCodecs.VEC2_CODEC.codec(), UUIDUtil.CODEC).fieldOf("cards").forGetter(HomeDashboard::cards)
	).apply(instance, HomeDashboard::new));

	public static final Codec<List<HomeDashboard>> LIST_CODEC = Codec.list(CODEC.codec());
	public static final StreamCodec<RegistryFriendlyByteBuf, HomeDashboard> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, HomeDashboard::id,
			ByteBufCodecs.STRING_UTF8, HomeDashboard::label,
			ResourceLocation.STREAM_CODEC, HomeDashboard::icon,
			ByteBufCodecs.map(HashMap::new, MoreCodecs.VEC2_STREAM_CODEC, UUIDUtil.STREAM_CODEC), HomeDashboard::cards,
			HomeDashboard::new
	);
}
