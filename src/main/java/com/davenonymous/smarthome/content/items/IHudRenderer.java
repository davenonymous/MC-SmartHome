package com.davenonymous.smarthome.content.items;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface IHudRenderer {
	void renderHud(GuiGraphics guiGraphics, Font font, ItemStack heldItem, DeltaTracker partialTick);
}
