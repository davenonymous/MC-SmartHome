package com.davenonymous.smarthome.blocks.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public abstract class HomeBlockEntity extends BaseBlockEntity {
	public static final UUID emptyUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private UUID ownerUUID = null;
	private UUID home = null;

	public HomeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public UUID home() {
		return home;
	}

	public void setHome(UUID home) {
		this.home = home;
	}

	public UUID ownerUUID() {
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if(tag.contains("owner")) {
			ownerUUID = tag.getUUID("owner");
		}
		if(tag.contains("home")) {
			home = tag.getUUID("home");
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if(ownerUUID != null) {
			tag.putUUID("owner", ownerUUID);
		}
		if(home != null) {
			tag.putUUID("home", home);
		}
	}

	public boolean isOwner(Player player) {
		return player != null && ownerUUID != null && ownerUUID.equals(player.getUUID());
	}
}
