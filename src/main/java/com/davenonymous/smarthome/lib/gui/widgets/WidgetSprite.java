package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.SpriteSizeCache;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class WidgetSprite extends Widget {
	ResourceLocation sprite;
	GuiTheme.SpriteComponent component;

	public WidgetSprite(ResourceLocation sprite) {
		this.sprite = sprite;
		autoSize();
	}

	public WidgetSprite(GuiTheme.SpriteComponent component) {
		this(SmartHome.sprite(component));
		this.component = component;
	}

	private WidgetSprite autoSize() {
		var size = SpriteSizeCache.getSpriteSize(sprite);
		if(size != null) {
			this.setSize(size.width, size.height);
		}
		return this;
	}

	public WidgetSprite setComponent(GuiTheme.SpriteComponent component) {
		this.component = component;
		this.sprite = SmartHome.sprite(component);
		autoSize();
		return this;
	}

	public WidgetSprite setSprite(ResourceLocation sprite) {
		this.sprite = sprite;
		this.component = null;
		autoSize();
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		pGuiGraphics.blitSprite(sprite, 0, 0, this.width(), this.height());
	}
}
