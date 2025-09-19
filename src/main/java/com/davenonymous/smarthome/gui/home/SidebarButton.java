package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class SidebarButton extends WidgetPanel {
	ResourceLocation icon;
	String label;

	WidgetSprite iconWidget;
	WidgetTextBox labelWidget;
	WidgetTextBox labelBackdropWidget;

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

		updateWidgetSizes();
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
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		if(isHovered()) {
			guiGraphics.fill(0, 0, this.width(), this.height(), 0x404420F0);
		}
		RenderSystem.enableBlend();
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_BORDER), 0, 0, this.width, this.height);
		RenderSystem.disableBlend();
		super.draw(guiGraphics, screen);
	}

}
