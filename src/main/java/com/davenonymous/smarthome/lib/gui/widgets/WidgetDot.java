package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.Icons;
import com.davenonymous.smarthome.lib.gui.WidgetScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class WidgetDot extends WidgetImage {
	public WidgetDot(int color) {
		super();
		this.setImage(getGUI().sprite(GuiTheme.SpriteComponent.WIDGET_DOT));
		this.setSize(10, 10);
		this.setTextureSize(10, 10);
		if(color > 0xFFFFFF) {
			color = color & 0x00FFFFFF;
			this.alpha = (color >> 24 & 0xFF) / 255.0F;
		}
		this.setColor(color);
		this.setAlpha(1.0f);
	}

	public WidgetDot(ChatFormatting color) {
		this(color.getColor());
	}
}
