package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorldSavedHomes extends SavedData {
	Map<UUID, List<HomeCore>> playerHomes;

	public static WorldSavedHomes get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			new Factory<WorldSavedHomes>(WorldSavedHomes::new, WorldSavedHomes::new),
			"smarthome_homes"
		);
	}

	public WorldSavedHomes() {
		this.playerHomes = new HashMap<>();
	}

	public WorldSavedHomes(CompoundTag nbt, HolderLookup.Provider provider) {
		this.playerHomes = new HashMap<>();
		if(nbt.contains("homes")) {
			Optional<WorldSavedHomes> decoded = CODEC.codec().parse(NbtOps.INSTANCE, nbt.get("homes")).result();
			if(decoded.isPresent()) {
				this.playerHomes.putAll(decoded.get().playerHomes);
			}
		}
		this.setHomeOwners();
	}

	public WorldSavedHomes(Map<UUID, List<HomeCore>> playerHomes) {
		this.playerHomes = new HashMap<>();
		if(playerHomes != null) {
			this.playerHomes.putAll(playerHomes);
		}
		this.setHomeOwners();
	}

	private void setHomeOwners() {
		for(var entry : playerHomes.entrySet()) {
			UUID playerId = entry.getKey();
			for(var home : entry.getValue()) {
				home.setOwner(playerId);
			}
		}
	}

	public WorldSavedHomes addHome(Player player, HomeCore home) {
		UUID playerId = player.getUUID();
		home.setOwner(playerId);
		List<HomeCore> homes = playerHomes.computeIfAbsent(playerId, key -> new ArrayList<>());
		homes.removeIf(h -> h.name().equals(home.name()));
		homes.add(home);
		this.setDirty();
		return this;
	}

	public WorldSavedHomes removeHome(Player player, String homeName) {
		UUID playerId = player.getUUID();
		List<HomeCore> homes = playerHomes.get(playerId);
		if(homes != null) {
			homes.removeIf(h -> h.name().equals(homeName));
			if(homes.isEmpty()) {
				playerHomes.remove(playerId);
			}
			this.setDirty();
		}
		return this;
	}

	public Optional<HomeCore> getHome(Player player, String homeName) {
		UUID playerId = player.getUUID();
		List<HomeCore> homes = playerHomes.get(playerId);
		if(homes != null) {
			return homes.stream().filter(h -> h.name().equals(homeName)).findFirst();
		}
		return Optional.empty();
	}

	public List<HomeCore> getHomes(Player player) {
		UUID playerId = player.getUUID();
		return playerHomes.getOrDefault(playerId, Collections.emptyList());
	}

	public Map<UUID, List<HomeCore>> playerHomes() {
		return playerHomes;
	}

	public static final MapCodec<WorldSavedHomes> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.unboundedMap(UUIDUtil.STRING_CODEC, HomeCore.CODEC.codec().listOf()).fieldOf("playerHomes").forGetter(WorldSavedHomes::playerHomes)
	).apply(instance, WorldSavedHomes::new));

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
		Optional<Tag> encoded = CODEC.codec().encodeStart(NbtOps.INSTANCE, this).result();
		encoded.ifPresent(tag -> compoundTag.put("homes", tag));
		return compoundTag;
	}
}
