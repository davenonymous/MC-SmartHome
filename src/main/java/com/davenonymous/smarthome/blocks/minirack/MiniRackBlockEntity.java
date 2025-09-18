package com.davenonymous.smarthome.blocks.minirack;

import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.lib.DimPos;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;

public class MiniRackBlockEntity extends HomeBlockEntity {
	public MiniRackInventory rackInventory;

	public MiniRackBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.MINI_RACK_ENTITY.get(), pos, blockState);
		this.rackInventory = new MiniRackInventory(slot -> {
			updateWorldSavedData();
			updateRacksInBlockState();
			setChanged();
		});
	}

	private void updateRacksInBlockState() {
		if(level == null || level.isClientSide()) {
			return;
		}

		boolean changed = false;
		var current = getBlockState();
		for(int i = 0; i < MiniRackBlock.SLOTS.length; i++) {
			var slotFilled = !rackInventory.getStackInSlot(i).isEmpty();
			var oldValue = current.getValue(MiniRackBlock.SLOTS[i]);
			if(oldValue == slotFilled) {
				continue;
			}
			current = current.setValue(MiniRackBlock.SLOTS[i], slotFilled);
			changed = true;
		}

		if(!changed) {
			return;
		}

		level.setBlockAndUpdate(worldPosition, current);
	}

	private void updateWorldSavedData() {
		if(level == null || level.isClientSide()) {
			return;
		}

		WorldSavedHomes data = WorldSavedHomes.get((ServerLevel) level);
		var homes = data.getPlayerHomes(ownerUUID());

		var serverStack = rackInventory.getServerStack();
		if(!serverStack.isEmpty() && serverStack.has(ModDataComponents.SERVER_DATA_COMPONENT)) {
			var serverData = serverStack.get(ModDataComponents.SERVER_DATA_COMPONENT);
			var existingHome = homes.stream().filter(home -> home.id().equals(serverData.id())).findFirst();
			var location = new DimPos(getLevel(), worldPosition);
			if(existingHome.isPresent()) {
				existingHome.get().setServerLocation(location);
				existingHome.get().setName(serverData.name());
			} else {
				data.addHome(new HomeCore(serverData.id(), serverData.owner(), location, serverData.name(), new ArrayList<>()));
			}

			data.setDirty();
		}

	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if(tag.contains("RackInventory")) {
			rackInventory.deserializeNBT(registries, tag.getCompound("RackInventory"));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.put("RackInventory", rackInventory.serializeNBT(registries));
	}

	@Override
	protected void applyImplicitComponents(DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);
		rackInventory.applyImplicitComponents(componentInput.get(DataComponents.CONTAINER));
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		rackInventory.collectImplicitComponents(components);
	}
}
