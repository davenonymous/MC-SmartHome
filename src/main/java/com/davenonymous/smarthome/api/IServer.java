package com.davenonymous.smarthome.api;

import net.minecraft.world.item.ItemStack;

public interface IServer extends IRackable {

	@Override
	default int getMaxPerRack(ItemStack stack) {
		return 1;
	}
}
