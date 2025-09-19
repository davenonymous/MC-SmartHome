package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class DevicesWidget extends WidgetPanel {
	public DevicesWidget() {
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		super.draw(guiGraphics, screen);
		guiGraphics.fill(0, 0, this.width, this.height, 0x88800000);
	}
}
