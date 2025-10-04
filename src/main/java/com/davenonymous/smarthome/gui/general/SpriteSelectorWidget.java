package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.events.SpriteSelectedEvent;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.BoxAlignment;
import com.davenonymous.smarthome.lib.gui.tooltip.StringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SpriteSelectorWidget extends WidgetPanel {
	WidgetPanel spriteGrid;

	public static SpriteSelectorWidget openAt(int x, int y, ContentAlignment alignment) {
		SpriteSelectorWidget widget = new SpriteSelectorWidget();
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

	public SpriteSelectorWidget() {
		super();
		this.setSize(120, 100);

		int padding = 4;
		int yOffset = padding;
		int xOffset = padding;
		int currentRowHeight = 0;

		spriteGrid = new WidgetPanel();
		spriteGrid.setWidth(this.width-padding*2);
		for(var entry : HackerNoon.Solid.allIcons.sequencedEntrySet()) {
			String name = entry.getKey();
			ResourceLocation icon = entry.getValue();
			ResourceLocation iconRegular = HackerNoon.Regular.allIcons.get(name);

			WidgetSprite iconWidget = new WidgetSprite(icon);
			iconWidget.setScale(0.5f);
			iconWidget.setColor(0xFFFFFFFF, ChatFormatting.DARK_GREEN.getColor() | 0xFF000000);
			iconWidget.setTooltipElements(
				StringTooltipComponent.orange(name),
				StringTooltipComponent.gray(iconWidget.width() + "x" + iconWidget.height())
			);
			iconWidget.setPosition(xOffset, yOffset);
			iconWidget.addListener(MouseClickEvent.class, (event, widget) -> {
				if(!widget.isHovered()) {
					return WidgetEventResult.CONTINUE_PROCESSING;
				}

				this.fireEvent(new SpriteSelectedEvent(icon));
				return WidgetEventResult.HANDLED;
			});

			spriteGrid.add(iconWidget);
			xOffset += iconWidget.width + padding;
			currentRowHeight = Math.max(currentRowHeight, iconWidget.height);

			if(iconRegular != null) {
				WidgetSprite iconWidgetRegular = new WidgetSprite(iconRegular);
				iconWidgetRegular.setScale(0.5f);
				iconWidgetRegular.setColor(0xFFFFFFFF, ChatFormatting.DARK_GREEN.getColor() | 0xFF000000);
				iconWidgetRegular.setPosition(xOffset, yOffset);
				iconWidgetRegular.setTooltipElements(
					StringTooltipComponent.orange(name + " (regular)"),
					StringTooltipComponent.gray(iconWidgetRegular.width() + "x" + iconWidgetRegular.height())
				);
				iconWidgetRegular.addListener(MouseClickEvent.class, (event, widget) -> {
					if(!widget.isHovered()) {
						return WidgetEventResult.CONTINUE_PROCESSING;
					}

					this.fireEvent(new SpriteSelectedEvent(iconRegular));
					return WidgetEventResult.HANDLED;
				});
				spriteGrid.add(iconWidgetRegular);
				xOffset += iconWidgetRegular.width + padding;
				currentRowHeight = Math.max(currentRowHeight, iconWidgetRegular.height);
			}


			if(xOffset + iconWidget.width + padding > this.width) {
				xOffset = padding;
				yOffset += currentRowHeight + padding;
				currentRowHeight = 0;
			}
		}

		spriteGrid.setHeight(yOffset + currentRowHeight + padding);

		var wrap = new ScissorScrollWrap(spriteGrid);
		wrap.setPosition(0, padding);
		wrap.setWidth(this.width- padding*2);
		wrap.setHeight(this.height - padding*2);
		this.add(wrap);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
