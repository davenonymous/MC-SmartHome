package com.davenonymous.smarthome.blocks.minirack;

import com.davenonymous.smarthome.api.IRackable;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class MiniRackBlockEntity extends HomeBlockEntity {
	public ItemStackHandler rackInventory = createRackInventory();

	public MiniRackBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.MINI_RACK_ENTITY.get(), pos, blockState);
	}

	private ItemStackHandler createRackInventory() {
		return new ItemStackHandler(MiniRackBlock.SLOTS.length) {
			@Override
			protected void onContentsChanged(int slot) {
				updateRacksInBlockState();
				setChanged();
			}

			@Override
			public int getSlotLimit(int slot) {
				return 1;
			}

			@Override
			public boolean isItemValid(int slot, ItemStack stack) {
				return stack.getItem() instanceof IRackable;
			}
		};
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

	@Override
	protected void applyImplicitComponents(DataComponentInput componentInput) {
		super.applyImplicitComponents(componentInput);

		if(componentInput.get(DataComponents.CONTAINER) instanceof ItemContainerContents container) {
			for(int slot = 0; slot < MiniRackBlock.SLOTS.length; slot++) {
				rackInventory.setStackInSlot(slot, container.getStackInSlot(slot).copy());
			}
		}
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);

		components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(
			rackInventory.getStackInSlot(0),
			rackInventory.getStackInSlot(1),
			rackInventory.getStackInSlot(2),
			rackInventory.getStackInSlot(3)
		)));
	}
}
