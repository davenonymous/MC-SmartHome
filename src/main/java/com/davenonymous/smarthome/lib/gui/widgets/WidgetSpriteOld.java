package com.davenonymous.smarthome.lib.gui.widgets;

import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class WidgetSpriteOld extends Widget {
	ResourceLocation spriteSheet;
	int u;
	int v;
	int width;
	int height;

	public WidgetSpriteOld(int u, int v, int width, int height) {
		this(GUIHelper.tabIcons, u, v, width, height);
	}

	public WidgetSpriteOld(ResourceLocation spriteSheet, int u, int v, int width, int height) {
		this.spriteSheet = spriteSheet;
		this.u = u;
		this.v = v;
		this.width = width;
		this.height = height;
	}

	public WidgetSpriteOld applyFrom(WidgetSpriteOld other) {
		this.spriteSheet = other.spriteSheet;
		this.u = other.u;
		this.v = other.v;
		this.width = other.width;
		this.height = other.height;
		return this;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Screen screen) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, spriteSheet);
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		guiGraphics.blit(spriteSheet, 0, 0, u, v, width, height);
	}

	public static WidgetSpriteOld activeRedstoneTorch = new WidgetSpriteOld(36, 84, 4, 11);
	public static WidgetSpriteOld downButton = new WidgetSpriteOld(124, 84, 11, 7);
	public static WidgetSpriteOld upButton = new WidgetSpriteOld(135, 84, 11, 7);
	public static WidgetSpriteOld downButtonHover = new WidgetSpriteOld(146, 84, 11, 7);
	public static WidgetSpriteOld upButtonHover = new WidgetSpriteOld(157, 84, 11, 7);

	public static WidgetSpriteOld leftButton = new WidgetSpriteOld(124, 95, 7, 11);
	public static WidgetSpriteOld rightButton = new WidgetSpriteOld(131, 95, 7, 11);
	public static WidgetSpriteOld leftButtonHover = new WidgetSpriteOld(146, 95, 7, 11);
	public static WidgetSpriteOld rightButtonHover = new WidgetSpriteOld(151, 95, 7, 11);
}
