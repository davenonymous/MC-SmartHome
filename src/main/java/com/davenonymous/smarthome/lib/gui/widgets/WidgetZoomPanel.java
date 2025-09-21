package com.davenonymous.smarthome.lib.gui.widgets;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class WidgetZoomPanel extends WidgetPanelWithValue<Widget> {
	public float zoomFactor = 1.0f;

	public WidgetZoomPanel(Widget value) {
		super(value);
		this.eventListeners.clear();
		this.anyEventListener.clear();
		this.renderDebugOutlines = true;

		this.setDimensions(0, 0, value.width, value.height);

		this.add(value);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().scale(zoomFactor, zoomFactor, 1.0f);
		super.draw(guiGraphics, window);
		guiGraphics.pose().popPose();
	}

	@Override
	public Widget getHoveredWidget(int mouseX, int mouseY) {
		int scaledMouseX = Math.round((getMouseX()) / zoomFactor);
		int scaledMouseY = Math.round((getMouseY()) / zoomFactor);
		return super.getHoveredWidget(scaledMouseX, scaledMouseY);
	}
}
