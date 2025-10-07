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
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class SidebarButton extends WidgetPanel {
	ResourceLocation icon;
	String label;

	WidgetSprite iconWidget;
	WidgetTextBox labelWidget;
	SidebarBadge badgeWidget;

	ResourceLocation contentId;
	boolean active = false;

	public SidebarButton(ResourceLocation icon, String label) {
		this.setSize(120, 20);
		this.icon = icon;
		this.label = label;

		this.iconWidget = new WidgetSprite(icon, SmartHome.color(GuiTheme.ColorComponent.TEXT_SECONDARY));
		this.iconWidget.setSize(12, 12);
		this.add(this.iconWidget);

		this.labelWidget = new WidgetTextBox(label);
		this.labelWidget.setTextColor(SmartHome.color(GuiTheme.ColorComponent.TEXT_SECONDARY));
		this.labelWidget.setWordWrap(true);
		this.labelWidget.setFont(ModFonts.SAMSUNG);
		this.add(this.labelWidget);

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

		this.addListener(ContentSelectionEvent.class, (event, widget) -> {
			this.setActive(event.contentId().equals(this.contentId));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		updateWidgetSizes();
	}

	public boolean isActive() {
		return active;
	}

	public SidebarButton setActive(boolean active) {
		this.active = active;
		if(active) {
			this.labelWidget.setTextColor(SmartHome.color(GuiTheme.ColorComponent.TEXT_ACTIVE));
			this.iconWidget.setColor(SmartHome.color(GuiTheme.ColorComponent.TEXT_ACTIVE));
		} else {
			this.labelWidget.setTextColor(SmartHome.color(GuiTheme.ColorComponent.TEXT_SECONDARY));
			this.iconWidget.setColor(SmartHome.color(GuiTheme.ColorComponent.TEXT_SECONDARY) | 0xFF000000);
		}
		return this;
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

	public SidebarButton clearBadge() {
		if(this.badgeWidget != null) {
			this.remove(this.badgeWidget);
			this.badgeWidget = null;
		}
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

		var maxHeight = Math.max(iconHeight, this.labelWidget.height());
		this.setHeight(maxHeight + 7);

		this.labelWidget.setX(iconWidth + 2*padding);
		this.iconWidget.setX(padding);

		this.iconWidget.setY(1+(this.height - iconHeight) / 2);
		this.labelWidget.setY(1+(this.height - this.labelWidget.height()) / 2);
	}


	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(isHovered()) {
			if(isActive()) {
				guiGraphics.fill(0, 0, this.width(), this.height(), SmartHome.color(GuiTheme.ColorComponent.BUTTON_BG_ACTIVE_HOVER));
			} else {
				guiGraphics.fill(0, 0, this.width(), this.height(), SmartHome.color(GuiTheme.ColorComponent.BUTTON_BG_HOVER));
			}
		} else {
			if(isActive()) {
				guiGraphics.fill(0, 0, this.width(), this.height(), SmartHome.color(GuiTheme.ColorComponent.BUTTON_BG_ACTIVE));
			} else {
				guiGraphics.fill(0, 0, this.width(), this.height(), SmartHome.color(GuiTheme.ColorComponent.BUTTON_BG));
			}
		}
		RenderSystem.enableBlend();
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_BORDER), 0, 0, this.width, this.height);
		RenderSystem.disableBlend();
		super.draw(guiGraphics, window);
	}

}
