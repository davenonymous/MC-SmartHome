package com.davenonymous.smarthome.content.items;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public interface IWorldRenderer {
	void renderWorld(RenderLevelStageEvent event, ItemStack heldItem);
}
