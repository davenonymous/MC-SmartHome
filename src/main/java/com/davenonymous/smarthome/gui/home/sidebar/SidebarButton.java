package com.davenonymous.smarthome.gui.home.sidebar;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.events.ContentSelectionEvent;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SidebarButton extends WidgetPanel {
	ResourceLocation icon;
	String label;

	WidgetSprite iconWidget;
	WidgetTextBox labelWidget;
	WidgetTextBox labelBackdropWidget;
	SidebarBadge badgeWidget;

	ResourceLocation contentId;

	public SidebarButton(ResourceLocation icon, String label) {
		this.setSize(120, 20);
		this.icon = icon;
		this.label = label;

		this.iconWidget = new WidgetSprite(icon, ChatFormatting.DARK_GRAY.getColor() | 0xFF000000);
		this.iconWidget.scale = 0.5f;
		this.add(this.iconWidget);

		this.labelWidget = new WidgetTextBox(label);
		this.labelWidget.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.labelWidget.setWordWrap(true);
		this.labelWidget.setFont(ModFonts.MONKEY_FILLED);
		this.add(this.labelWidget);

		this.labelBackdropWidget = new WidgetTextBox(label);
		this.labelBackdropWidget.setTextColor(ChatFormatting.WHITE.getColor());
		this.labelBackdropWidget.setWordWrap(true);
		this.labelBackdropWidget.setFont(ModFonts.MONKEY_OUTLINE);
		this.labelBackdropWidget.setVisible(false);
		this.add(this.labelBackdropWidget);

		this.addListener(MouseClickEvent.class, (event, widget) -> {
			if(event.button != 0) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			if(this.contentId != null) {
				getGUI().fireEvent(new ContentSelectionEvent(this.contentId));
				return WidgetEventResult.HANDLED;
			}

			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		updateWidgetSizes();
	}

	public SidebarButton setBadge(String text, String description, int badgeColor, int textColor) {
		if(this.badgeWidget != null) {
			this.remove(this.badgeWidget);
		}
		this.badgeWidget = new SidebarBadge(text, badgeColor, textColor);
		this.badgeWidget.setPosition(this.width - this.badgeWidget.width - 5, 3);
		if(description != null && !description.isEmpty()) {
			this.badgeWidget.setTooltipElements(WrappedStringTooltipComponent.orange(description));
		}
		this.add(this.badgeWidget);
		return this;
	}

	public ResourceLocation contentId() {
		return contentId;
	}

	public SidebarButton setContentId(ResourceLocation contentId) {
		this.contentId = contentId;
		return this;
	}

	public void updateWidgetSizes() {
		var iconWidth = (int)(this.iconWidget.width * this.iconWidget.scale);
		var iconHeight = (int)(this.iconWidget.height * this.iconWidget.scale);
		int padding = 3;

		this.labelWidget.autoWidth(this.width - iconWidth - padding);
		this.labelWidget.autoHeight();
		this.labelBackdropWidget.autoWidth(this.width - iconWidth - padding);
		this.labelBackdropWidget.autoHeight();

		var maxHeight = Math.max(iconHeight, this.labelWidget.height());
		this.setHeight(maxHeight + 7);

		this.labelWidget.setX(iconWidth + 2*padding);
		this.labelBackdropWidget.setX(iconWidth + 2*padding);
		this.iconWidget.setX(padding);

		this.iconWidget.setY((this.height - iconHeight) / 2);
		this.labelWidget.setY(1+(this.height - this.labelWidget.height()) / 2);
		this.labelBackdropWidget.setY(1+(this.height - this.labelBackdropWidget.height()) / 2);
	}


	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(isHovered()) {
			guiGraphics.fill(0, 0, this.width(), this.height(), 0x404420F0);
		} else {
			guiGraphics.fill(0, 0, this.width(), this.height(), 0x50808080);
		}
		RenderSystem.enableBlend();
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_BORDER), 0, 0, this.width, this.height);
		RenderSystem.disableBlend();
		super.draw(guiGraphics, window);
	}

}
