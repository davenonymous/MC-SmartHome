package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.SpriteSizeCache;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class WidgetSprite extends Widget {
	ResourceLocation sprite;
	GuiTheme.SpriteComponent component;
	int color;

	public WidgetSprite(ResourceLocation sprite) {
		this(sprite, 0xFFFFFFFF);
	}

	public WidgetSprite(GuiTheme.SpriteComponent component) {
		this(component, 0xFFFFFFFF);
	}

	public WidgetSprite(ResourceLocation sprite, int color) {
		this.sprite = sprite;
		this.color = color;
		autoSize();
	}

	public WidgetSprite(GuiTheme.SpriteComponent component, int color) {
		this(SmartHome.sprite(component), color);
		this.component = component;
	}

	public WidgetSprite autoSize() {
		var size = SpriteSizeCache.getSpriteSize(sprite);
//		if(scale != 1.0f) {
//			size = new Size2i((int)(size.width * scale), (int)(size.height * scale));
//		}
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

	public int color() {
		return color;
	}

	public WidgetSprite setColor(int color) {
		this.color = color;
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		RenderSystem.enableBlend();
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		RenderSystem.setShaderColor(r, g, b, alpha);
		pGuiGraphics.blitSprite(sprite, 0, 0, this.width(), this.height());
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		RenderSystem.disableBlend();
	}
}
