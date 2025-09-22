package com.davenonymous.smarthome.items;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public interface IWorldRenderer {
	default RenderLevelStageEvent.Stage renderStage() {
		return RenderLevelStageEvent.Stage.AFTER_WEATHER;
	}

	void renderWorld(RenderLevelStageEvent event, ItemStack heldItem);
}
