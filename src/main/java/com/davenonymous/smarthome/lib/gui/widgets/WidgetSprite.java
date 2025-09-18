package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.WidgetScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class WidgetSprite extends Widget {
	ResourceLocation sprite;
	GuiTheme.SpriteComponent component;

	public WidgetSprite(ResourceLocation sprite) {
		this.sprite = sprite;
	}

	public WidgetSprite(GuiTheme.SpriteComponent component) {
		this.component = component;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		var toDraw = sprite;
		if(component != null && screen instanceof WidgetScreen widgetScreen) {
			toDraw = widgetScreen.theme().getSprite(component);
		}

		pGuiGraphics.blitSprite(toDraw, 0, 0, this.width(), this.height());
	}
}
