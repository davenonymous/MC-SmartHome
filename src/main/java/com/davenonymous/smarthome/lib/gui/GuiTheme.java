package com.davenonymous.smarthome.lib.gui;

import net.minecraft.resources.ResourceLocation;

public abstract class GuiTheme {
	public enum SpriteComponent {
		WINDOW_BACKGROUND,
		WINDOW_PUSHED_BACKGROUND,

		WIDGET_PREV,
		WIDGET_NEXT,

		WIDGET_DOT,
		WIDGET_TOGGLE_ON,
		WIDGET_TOGGLE_OFF,
		WIDGET_TOGGLE_AUTO,

		SLOT,
	}

	public abstract ResourceLocation getSprite(SpriteComponent component);


}
