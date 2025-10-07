package com.davenonymous.smarthome.lib.gui.widgets;


import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.DyeColor;

public class WidgetColorSelect extends WidgetWithChoiceValue<Integer> {

	public WidgetColorSelect(int selected) {
		this();
		if(!choices.contains(selected)) {
			choices.add(selected);
		}
		this.setValue(selected);
	}

	public WidgetColorSelect() {
		this.setHeight(10);
		this.setWidth(10);

		for(var color : DyeColor.values()) {
			this.addChoice(color.getTextColor() | 0xFF000000);
		}

		this.setValue(DyeColor.ORANGE.getTextColor());
		this.addClickListener();
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		super.draw(pGuiGraphics, window);

		int activeColor = this.getValue() != null ? this.getValue() : 0xFFFFFFFF;
		GUIHelper.setShaderColor(activeColor);
		pGuiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WIDGET_DOT), 0, 0, this.width, this.height);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
