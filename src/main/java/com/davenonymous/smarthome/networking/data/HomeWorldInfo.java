package com.davenonymous.smarthome.networking.data;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.setup.WorldWatcher;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record HomeWorldInfo(Map<BlockPos, BlockState> blockStates) {

	public static HomeWorldInfo create(ServerLevel level, HomeCore home) {
		Map<BlockPos, BlockState> blocksInZones = home.zones().stream()
			.flatMap(zone -> WorldWatcher.getBlocksInAABBStream(zone.bounds()).stream())
			.distinct()
			.filter(level::isLoaded)
			.filter(Predicate.not(level::isEmptyBlock))
			.collect(Collectors.toMap(Function.identity(),level::getBlockState));

		return new HomeWorldInfo(blocksInZones);
	}

	public static HomeWorldInfo empty() { return new HomeWorldInfo(Map.of()); }

	public static final MapCodec<HomeWorldInfo> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.unboundedMap(BlockPos.CODEC, BlockState.CODEC).fieldOf("blocks").forGetter(HomeWorldInfo::blockStates)
	).apply(instance, HomeWorldInfo::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeWorldInfo> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.map(
			HashMap::new,
			BlockPos.STREAM_CODEC,
			ByteBufCodecs.fromCodec(BlockState.CODEC)
		), HomeWorldInfo::blockStates,
		HomeWorldInfo::new
	);
}
