package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

public class SidebarWidget extends WidgetVBox {
	public SidebarWidget(HomeScreen screen) {
		this.setWidth(120);
		this.setPadding(10);
		this.setSpacing(10);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		super.draw(guiGraphics, screen);
		guiGraphics.fill(0, 0, this.width(), this.height(), 0xFF4420F0);
	}
}
