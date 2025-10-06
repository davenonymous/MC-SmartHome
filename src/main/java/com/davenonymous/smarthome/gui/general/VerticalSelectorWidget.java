package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseExitEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class VerticalSelectorWidget extends WidgetPanel {
	WidgetPanel choicesList;

	public static VerticalSelectorWidget openAt(int x, int y, ContentAlignment alignment, Widget... choices) {
		VerticalSelectorWidget widget = new VerticalSelectorWidget(choices);
		int inset = 16;

		int chosenX = x - (widget.width() / 2);
		int chosenY = y - (widget.height() / 2);
		if(alignment.isTop) {
			chosenY = y - (widget.height() - inset);
		} else if(alignment.isBottom) {
			chosenY = y - inset;
		}
		if(alignment.isLeft) {
			chosenX = x - (widget.width() - inset);
		} else if(alignment.isRight) {
			chosenX = x - inset;
		}
		widget.setPosition(chosenX, chosenY);
		return widget;
	}

	public VerticalSelectorWidget(Widget... choices) {
		super();
		this.setSize(120, 100);

		int padding = 4;

		choicesList = new WidgetPanel();
		choicesList.setWidth(this.width-padding*2);

		int yOffset = padding;
		for(var choice : choices) {
			choicesList.add(choice);
			choice.setPosition(padding, yOffset);
			choice.setWidth(choicesList.width - padding*2);
			yOffset += choice.height();
		}
		choicesList.adjustSizeToContent(false);

		var wrap = new ScissorScrollWrap(choicesList);
		wrap.setPosition(0, padding);
		wrap.setWidth(this.width- padding*2);
		wrap.setHeight(this.height - padding*2);
		this.add(wrap);

		this.addListener(
			MouseExitEvent.class, (event2, widget2) -> {
				if(this.getParent() instanceof WidgetPanel parentPanel) {
					parentPanel.remove(this);
				}
				return WidgetEventResult.HANDLED;
			});
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
