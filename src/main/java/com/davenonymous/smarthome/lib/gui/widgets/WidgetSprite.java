package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.*;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class WidgetSprite extends Widget {
	ResourceLocation sprite;
	GuiTheme.SpriteComponent component;
	int color;
	int hoverColor;

	List<Animation> animations;

	public WidgetSprite(ResourceLocation sprite, int color, int hoverColor) {
		this.sprite = sprite;
		this.color = color;
		this.hoverColor = hoverColor;
		this.animations = new ArrayList<>();
		autoSize();
	}

	public WidgetSprite(ResourceLocation sprite) {
		this(sprite, 0xFFFFFFFF);
	}

	public WidgetSprite(GuiTheme.SpriteComponent component) {
		this(component, 0xFFFFFFFF);
	}

	public WidgetSprite(ResourceLocation sprite, int color) {
		this(sprite, color, color);
	}

	public WidgetSprite(GuiTheme.SpriteComponent component, int color) {
		this(SmartHome.sprite(component), color, color);
		this.component = component;
	}

	public WidgetSprite(GuiTheme.SpriteComponent component, int color, int hoverColor) {
		this(SmartHome.sprite(component), color, hoverColor);
		this.component = component;
	}

	public WidgetSprite autoSize() {
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

	public WidgetSprite addAnimation(Animation animation) {
		this.animations.add(animation);
		return this;
	}

	public WidgetSprite clearAnimations() {
		this.animations.clear();
		return this;
	}

	public int color() {
		return color;
	}

	public WidgetSprite setColor(int color) {
		this.color = color;
		this.hoverColor = color;
		return this;
	}

	public WidgetSprite setColor(int color, int hoverColor) {
		this.color = color;
		this.hoverColor = hoverColor;
		return this;
	}

	public WidgetSprite setScale(float scale) {
		var size = SpriteSizeCache.getSpriteSize(sprite);
		if(size != null) {
			this.setSize((int)(size.width * scale), (int)(size.height * scale));
		}
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {

		var colorToUse = this.color;
		if(this.isHovered()) {
			colorToUse = this.hoverColor;
		}

		float alpha = (colorToUse >> 24 & 0xFF) / 255.0F;
		float r = (colorToUse >> 16 & 0xFF) / 255.0F;
		float g = (colorToUse >> 8 & 0xFF) / 255.0F;
		float b = (colorToUse & 0xFF) / 255.0F;

		float partialTicks = 0f;
		var screen = Minecraft.getInstance().screen;
		if(screen == null) {
			partialTicks = RenderSystem.getShaderGameTime();
		} else if(screen instanceof WidgetScreen widgetScreen) {
			partialTicks = widgetScreen.partialTicks() + widgetScreen.renderTick();
		} else if(screen instanceof WidgetContainerScreen<?> widgetScreen) {
			partialTicks = widgetScreen.partialTicks() + widgetScreen.renderTick();
		}

		var pose = pGuiGraphics.pose();
		pose.pushPose();

		// always rotate
		pose.translate(this.width()/2.0f, this.height()/2.0f, 0);
		for(var anim : animations) {
			anim.applyTransform(pGuiGraphics, partialTicks);
		}
		pose.translate(-this.width()/2.0f, -this.height()/2.0f, 0);

		RenderSystem.setShaderColor(r, g, b, alpha);
		RenderSystem.enableBlend();
		var guiScale = window.getGuiScale();
		if(guiScale == 3) {
			// Render the sprite at 2x scale and center it instead.
			int newScale = 2;
			pose.scale(newScale/3f, newScale/3f, 1.0f);
			pose.translate((this.width()/(float)newScale) * (1 - newScale/3f), (this.height()/2f) * (1 - 2/3f), 0);
		} else {
			RenderSystem.defaultBlendFunc();
		}
		pGuiGraphics.blitSprite(sprite, 0, 0, this.width(), this.height());
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		pose.popPose();

	}
}
