package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.event.WidgetSizeChangeEvent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class WidgetFlowBox extends WidgetPanel {
	int spacingHorizontal = 4;
	int spacingVertical = 4;

	int paddingHorizontal = 0;
	int paddingVertical = 0;

	public WidgetFlowBox() {
		super();

		this.addListener(WidgetSizeChangeEvent.class, ((event, widget) -> {
			updateWidgetSizes();
			return WidgetEventResult.CONTINUE_PROCESSING;
		}));
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 0);
		super.draw(guiGraphics, window);
		guiGraphics.pose().popPose();
	}

	public WidgetFlowBox setPadding(int padding) {
		setPaddingHorizontal(padding);
		setPaddingVertical(padding);
		return this;
	}

	public WidgetFlowBox setPaddingHorizontal(int paddingHorizontal) {
		this.paddingHorizontal = paddingHorizontal;
		return this;
	}

	public WidgetFlowBox setPaddingVertical(int paddingVertical) {
		this.paddingVertical = paddingVertical;
		return this;
	}

	public WidgetFlowBox setSpacing(int spacing) {
		setSpacingHorizontal(spacing);
		setSpacingVertical(spacing);
		return this;
	}

	public WidgetFlowBox setSpacingHorizontal(int spacingHorizontal) {
		this.spacingHorizontal = spacingHorizontal;
		return this;
	}

	public WidgetFlowBox setSpacingVertical(int spacingVertical) {
		this.spacingVertical = spacingVertical;
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		int maxWidth = this.width - 2 * paddingHorizontal;
		int currentX = paddingHorizontal;
		int currentY = paddingVertical;
		int rowHeight = 0;

		for(Widget child : this.children()) {
			if(!child.isVisible()) {
				continue;
			}

			if(currentX + child.width > maxWidth) {
				// Wrap to next line
				currentX = paddingHorizontal;
				currentY += rowHeight + spacingVertical;
				rowHeight = 0;
			}

			child.setPosition(currentX, currentY);
			currentX += child.width + spacingHorizontal;
			rowHeight = Math.max(rowHeight, child.height);
		}
	}
}
