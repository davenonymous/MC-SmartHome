package com.davenonymous.smarthome.lib.gui.theme;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import net.minecraft.resources.ResourceLocation;

public class Vanilla extends GuiTheme {
	public static ResourceLocation guiDot = SmartHome.resource("vanilla/dot");

	public static ResourceLocation toggleOn = SmartHome.resource("vanilla/toggle_on");
	public static ResourceLocation toggleOff = SmartHome.resource("vanilla/toggle_off");
	public static ResourceLocation toggleAuto = SmartHome.resource("vanilla/toggle_auto");

	public static ResourceLocation window = SmartHome.resource("vanilla/window");
	public static ResourceLocation windowPushed = SmartHome.resource("vanilla/window_pushed");

	public static ResourceLocation slot = SmartHome.resource("vanilla/slot");

	public static ResourceLocation prev = SmartHome.resource("vanilla/prev");
	public static ResourceLocation next = SmartHome.resource("vanilla/next");


	@Override
	public ResourceLocation getSprite(SpriteComponent component) {
		return switch(component) {
			case WIDGET_DOT -> guiDot;
			case WIDGET_TOGGLE_ON -> toggleOn;
			case WIDGET_TOGGLE_OFF -> toggleOff;
			case WIDGET_TOGGLE_AUTO -> toggleAuto;
			case WINDOW_BACKGROUND -> window;
			case WINDOW_PUSHED_BACKGROUND -> windowPushed;
			case SLOT -> slot;
			case WIDGET_PREV -> prev;
			case WIDGET_NEXT -> next;
		};
	}
}
