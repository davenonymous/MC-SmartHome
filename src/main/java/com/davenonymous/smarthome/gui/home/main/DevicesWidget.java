package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class DevicesWidget extends WidgetPanel {
	public DevicesWidget() {
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		super.draw(guiGraphics, window);
		guiGraphics.fill(0, 0, this.width, this.height, 0x88800000);
	}
}
