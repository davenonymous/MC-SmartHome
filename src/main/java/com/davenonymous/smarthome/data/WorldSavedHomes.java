package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
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
	Map<UUID, HomeCore> homeByUUID;

	public static WorldSavedHomes get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(
			new Factory<WorldSavedHomes>(WorldSavedHomes::new, WorldSavedHomes::new),
			"smarthome_homes"
		);
	}

	public Optional<HomeZone> getHome(BlockPos pos) {
		for(var home : homeByUUID.values()) {
			if(home.contains(pos)) {
				var zone = home.getZoneContaining(pos);
				if(zone != null) {
					return Optional.of(zone);
				}
			}
		}

		return Optional.empty();
	}

	public WorldSavedHomes() {
		this.homeByUUID = new HashMap<>();
	}

	public WorldSavedHomes(CompoundTag nbt, HolderLookup.Provider provider) {
		this();

		if(nbt.contains("homes")) {
			Optional<WorldSavedHomes> decoded = CODEC.codec().parse(NbtOps.INSTANCE, nbt.get("homes")).result();
			if(decoded.isPresent()) {
				this.homeByUUID.putAll(decoded.get().homeByUUID);
			}
		}
		this.updateHomeCaches();
	}

	public WorldSavedHomes(Map<UUID, HomeCore> homes) {
		this();

		if(homes != null) {
			this.homeByUUID.putAll(homes);
		}
		this.updateHomeCaches();
	}

	private void updateHomeCaches() {
		for(var entry : homeByUUID.entrySet()) {
			UUID playerId = entry.getKey();
			HomeCore home = entry.getValue();
			home.setOwner(playerId);
		}
	}

	public WorldSavedHomes addHome(HomeCore home) {
		homeByUUID.put(home.id(), home);
		this.setDirty();
		return this;
	}

	public WorldSavedHomes removeHome(UUID playerId, String homeName) {
		var home = this.getHome(playerId, homeName);
		if(home.isPresent()) {
			homeByUUID.remove(home.get().id());
			this.setDirty();
		}
		return this;
	}

	public Optional<HomeCore> getHome(Player player, String homeName) {
		return getHome(player.getUUID(), homeName);
	}

	public Optional<HomeCore> getHome(UUID playerId, String homeName) {
		return homeByUUID.values().stream()
			.filter(h -> h.owner() != null && h.owner().equals(playerId) && h.name().equals(homeName))
			.findFirst();
	}

	public List<HomeCore> getHomes(UUID playerId) {
		return homeByUUID.values().stream()
			.filter(h -> h.owner() != null && h.owner().equals(playerId))
			.toList();
	}

	public Map<UUID, HomeCore> homes() {
		return homeByUUID;
	}

	public static final MapCodec<WorldSavedHomes> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.unboundedMap(UUIDUtil.STRING_CODEC, HomeCore.CODEC.codec()).fieldOf("homes").forGetter(WorldSavedHomes::homes)
	).apply(instance, WorldSavedHomes::new));

	@Override
	public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
		Optional<Tag> encoded = CODEC.codec().encodeStart(NbtOps.INSTANCE, this).result();
		encoded.ifPresent(tag -> compoundTag.put("homes", tag));
		return compoundTag;
	}

}
