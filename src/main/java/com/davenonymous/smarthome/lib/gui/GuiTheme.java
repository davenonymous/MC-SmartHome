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

	public enum ColorComponent {
		TEXT_PRIMARY,
		TEXT_PRIMARY_HOVER,

		TEXT_SECONDARY,
		TEXT_SECONDARY_HOVER,

		TEXT_ACTIVE,
		TEXT_ACTIVE_HOVER,

		BUTTON_BG,
		BUTTON_BG_HOVER,
		BUTTON_BG_ACTIVE,
		BUTTON_BG_ACTIVE_HOVER,
	}

	public abstract ResourceLocation getSprite(SpriteComponent component);

	public abstract int getColor(ColorComponent component);

}
