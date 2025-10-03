package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.Animation;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.SpriteSizeCache;
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

	List<Animation> animations;

	public WidgetSprite(ResourceLocation sprite) {
		this(sprite, 0xFFFFFFFF);
	}

	public WidgetSprite(GuiTheme.SpriteComponent component) {
		this(component, 0xFFFFFFFF);
	}

	public WidgetSprite(ResourceLocation sprite, int color) {
		this.sprite = sprite;
		this.color = color;
		this.animations = new ArrayList<>();
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
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		RenderSystem.enableBlend();
		float alpha = (color >> 24 & 0xFF) / 255.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;

		float partialTicks = HomeScreen.get().partialTicks() + HomeScreen.get().renderTick();

		var pose = pGuiGraphics.pose();
		pose.pushPose();

		// always rotate
		pose.translate(this.width()/2.0f, this.height()/2.0f, 0);
		for(var anim : animations) {
			anim.applyTransform(pGuiGraphics, partialTicks);
		}
		pose.translate(-this.width()/2.0f, -this.height()/2.0f, 0);

		RenderSystem.setShaderColor(r, g, b, alpha);
		pGuiGraphics.blitSprite(sprite, 0, 0, this.width(), this.height());
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

		pose.popPose();

		RenderSystem.disableBlend();
	}
}
