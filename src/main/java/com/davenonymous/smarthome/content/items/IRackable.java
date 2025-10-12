package com.davenonymous.smarthome.content.items;

import net.minecraft.world.item.ItemStack;

public interface IRackable {
	int MAX_RACK_SLOTS = 4;

	default int getMaxPerRack(ItemStack stack) {
		return MAX_RACK_SLOTS;
	}
}
