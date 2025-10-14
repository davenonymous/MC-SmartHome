package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.lib.gui.event.MouseScrollEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.event.WidgetSizeChangeEvent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class ScissorScrollWrap extends WidgetPanel {
	Widget wrappedWidget;
	float verticalScroll = 0;
	float horizontalScroll = 0;
	int contentOverflowX = 0;
	int contentOverflowY = 0;

	boolean needsHorizontalScrollbar = false;
	boolean needsVerticalScrollbar = false;

	public ScissorScrollWrap(Widget wrappedWidget) {
		this.wrappedWidget = wrappedWidget;
		this.wrappedWidget.addListener(WidgetSizeChangeEvent.class, (event, widget) -> {
			this.updateWidgetSizes();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.add(wrappedWidget);

		this.addListener(
			MouseScrollEvent.class, (event, widget) -> {

				if(!this.areAllParentsVisible()) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				float scrollDivider = 32f;
				if(widget.isPosInside(event.mouseX, event.mouseY)) {
					var gui = getGUI();
					var horizontal = gui.isCtrlDown();
					var scrollValue = Math.abs((int) Math.ceil(event.rawScrollValue));
					if(event.up) {
						if(horizontal) {
							this.horizontalScroll = Math.max(0, this.horizontalScroll - scrollValue / scrollDivider);
						} else {
							this.verticalScroll = Math.max(0, this.verticalScroll - scrollValue / scrollDivider);
						}
					} else {
						if(horizontal) {
							this.horizontalScroll = Math.min(1, this.horizontalScroll + scrollValue / scrollDivider);
						} else {
							this.verticalScroll = Math.min(1, this.verticalScroll + scrollValue / scrollDivider);
						}
					}

					contentOverflowX = Math.max(0, wrappedWidget.width() - this.width());
					contentOverflowY = Math.max(0, wrappedWidget.height() - this.height());

					float yOffset = contentOverflowY * verticalScroll;
					float xOffset = contentOverflowX * horizontalScroll;

					wrappedWidget.setPosition((int)-xOffset, (int)-yOffset);

					return WidgetEventResult.HANDLED;
				}

				return WidgetEventResult.CONTINUE_PROCESSING;
			}
		);

		this.updateWidgetSizes();
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		pGuiGraphics.enableScissor(getActualX(), getActualY(), getActualX() + this.width(), getActualY() + this.height());
		super.draw(pGuiGraphics, window);
		pGuiGraphics.disableScissor();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		wrappedWidget.updateWidgetSizes();

		contentOverflowX = Math.max(0, wrappedWidget.width() - this.width());
		contentOverflowY = Math.max(0, wrappedWidget.height() - this.height());

		needsHorizontalScrollbar = contentOverflowX > 0;
		needsVerticalScrollbar = contentOverflowY > 0;

		if(width() < 0) {
			this.setWidth(0);
		}
		if(height() < 0) {
			this.setHeight(0);
		}
	}
}
