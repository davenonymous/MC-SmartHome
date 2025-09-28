package com.davenonymous.smarthome.lib.gui;

import net.minecraft.resources.ResourceLocation;

public abstract class GuiTheme {
	public enum SpriteComponent {
		WINDOW_BACKGROUND,
		WINDOW_PUSHED_BACKGROUND,

		BUTTON_BORDER,
		BUTTON_R1,
		BUTTON_R2,
		BUTTON_R3,
		BUTTON_R4,

		WIDGET_PREV,
		WIDGET_NEXT,

		WIDGET_DOT,
		WIDGET_TOGGLE_ON,
		WIDGET_TOGGLE_OFF,
		WIDGET_TOGGLE_AUTO,

		SLOT;

		public static SpriteComponent getToggleSprite(boolean state) {
			return state ? WIDGET_TOGGLE_ON : WIDGET_TOGGLE_OFF;
		}
	}

	public abstract ResourceLocation getSprite(SpriteComponent component);


}
