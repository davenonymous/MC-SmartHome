package com.davenonymous.smarthome.gui.home.main.devices;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class AddDeviceButtonWidget extends WidgetPanel {
	public WidgetSprite icon;
	public int buttonColor = 0xFFFFFFFF;

	public AddDeviceButtonWidget(ResourceLocation iconId) {
		this.setWidth(21);
		this.setHeight(21);

		icon = new WidgetSprite(iconId);
		icon.setColor(0x80FFFFFF);
		icon.scale = 0.5f;
		icon.addListener(MouseEnterEvent.class, (event, widget) -> {
			icon.setColor(0xFFEEEEEE);
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		icon.addListener(MouseExitEvent.class, (event, widget) -> {
			icon.setColor(0x80FFFFFF);
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.add(icon);

		updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		icon.setPosition(4, 4);
	}

	public AddDeviceButtonWidget setIcon(ResourceLocation icon) {
		this.icon.setSprite(icon);
		return this;
	}

	public int buttonColor() {
		return buttonColor;
	}

	public AddDeviceButtonWidget setButtonColor(int buttonColor) {
		this.buttonColor = buttonColor;
		return this;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		var tint = ColorHelper.toVector(buttonColor);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(tint.x(), tint.y(), tint.z(), tint.w());
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R3), 0, 0, this.width, this.height);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.disableBlend();

		super.draw(guiGraphics, window);
	}
}
