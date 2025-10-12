package com.davenonymous.smarthome.content.blocks.minirack;

import com.davenonymous.smarthome.content.items.IRackable;
import com.davenonymous.smarthome.content.items.IServer;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.Consumer;

public class MiniRackInventory extends ItemStackHandler {
	private final Consumer<Integer> onContentsChanged;

	public MiniRackInventory(Consumer<Integer> onContentsChanged) {
		super(IRackable.MAX_RACK_SLOTS);
		this.onContentsChanged = onContentsChanged;
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		this.onContentsChanged.accept(slot);
	}

	public ItemStack getServerStack() {
		for(int i = 0; i < getSlots(); i++) {
			var stack = getStackInSlot(i);
			if(stack.isEmpty()) {
				continue;
			}
			if(stack.getItem() instanceof IServer) {
				return stack;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if(!(stack.getItem() instanceof IRackable rackable)) {
			return false;
		}

		int maxItemsInRack = rackable.getMaxPerRack(stack);
		if(maxItemsInRack <= 0) {
			return false;
		}

		if(maxItemsInRack < IRackable.MAX_RACK_SLOTS) {
			int existingItems = 1;
			for(int i = 0; i < getSlots(); i++) {
				var existingStack = getStackInSlot(i);
				if(existingStack.isEmpty()) {
					continue;
				}
				if(existingStack.getItem() == rackable) {
					existingItems++;
				}
			}

			if(existingItems > maxItemsInRack) {
				return false;
			}
		}

		return true;
	}

	public void applyImplicitComponents(ItemContainerContents container) {
		for(int slot = 0; slot < MiniRackBlock.SLOTS.length; slot++) {
			this.setStackInSlot(slot, container.getStackInSlot(slot).copy());
		}
	}

	public void collectImplicitComponents(DataComponentMap.Builder components) {
		components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(
			this.getStackInSlot(0),
			this.getStackInSlot(1),
			this.getStackInSlot(2),
			this.getStackInSlot(3)
		)));
	}
}
