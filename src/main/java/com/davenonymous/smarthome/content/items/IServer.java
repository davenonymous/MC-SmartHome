package com.davenonymous.smarthome.content.items;

import net.minecraft.world.item.ItemStack;

public interface IServer extends IRackable {

	@Override
	default int getMaxPerRack(ItemStack stack) {
		return 1;
	}
}
