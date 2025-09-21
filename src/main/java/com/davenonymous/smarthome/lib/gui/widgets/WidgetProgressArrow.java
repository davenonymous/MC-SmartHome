package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class WidgetProgressArrow extends WidgetWithRangeValue<Double> {
	public WidgetProgressArrow() {
		this.value = 0D;
		this.setRange(0D, 100D);
		this.setWidth(22);
		this.setHeight(15);
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		int spriteWidth = 22;
		int spriteHeight = 15;
		int spriteY = 84;
		int bgSpriteX = 42;
		int progressSpriteX = 102;

		//pGuiGraphics.blitSprite(GUI.tabIcons, spriteWidth, spriteHeight, bgSpriteX, spriteY, this.x, this.y, spriteWidth, spriteHeight);
		pGuiGraphics.blit(GUIHelper.tabIcons, 0, 0, bgSpriteX, spriteY, spriteWidth, spriteHeight);

		int progressWidth = (int) (spriteWidth * (this.value - this.rangeMin) / (this.rangeMax - this.rangeMin));
		pGuiGraphics.blit(GUIHelper.tabIcons, 0, 0, progressSpriteX, spriteY, progressWidth, spriteHeight + 1);
	}
}
