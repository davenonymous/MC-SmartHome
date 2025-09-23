package com.davenonymous.smarthome.items.projectbox;

import com.davenonymous.smarthome.blocks.minirack.MiniRackBlock;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.function.BiConsumer;

public class ProjectBoxInventoryHandler extends ItemStackHandler {
	private final BiConsumer<Integer, ItemStack> onContentsChanged;
	private final ProjectBoxDataComponent data;

	public ProjectBoxInventoryHandler(BiConsumer<Integer, ItemStack> onContentsChanged, ProjectBoxDataComponent data) {
		super(5);
		this.onContentsChanged = onContentsChanged;
		this.data = data;
		this.setStackInSlot(0, data.microController().copy());
		for(int i = 0; i < 4; i++) {
			this.setStackInSlot(i + 1, data.peripherals().get(i).copy());
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);
		this.onContentsChanged.accept(slot, this.getStackInSlot(slot));
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return slot < 5;
	}

	public void applyImplicitComponents(ItemContainerContents container) {
		for(int slot = 0; slot < MiniRackBlock.SLOTS.length; slot++) {
			this.setStackInSlot(slot, container.getStackInSlot(slot).copy());
		}
	}

	public void collectImplicitComponents(DataComponentMap.Builder components) {
		components.set(
			DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(
				this.getStackInSlot(0),
				this.getStackInSlot(1),
				this.getStackInSlot(2),
				this.getStackInSlot(3)
			)));
	}
}
