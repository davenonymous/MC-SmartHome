package com.davenonymous.smarthome.lib;

import com.davenonymous.smarthome.lib.gui.DynamicImageResources;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class CanvasWidget extends Widget {
	private int[][] canvas;
	private boolean needsRedraw;
	private DynamicImageResources.DynTexture renderedTexture;
	private final String canvasId;


	public CanvasWidget(String canvasId, int width, int height) {
		super();
		this.canvasId = canvasId;
		this.width = width;
		this.height = height;
		this.canvas = new int[width][height];
		this.needsRedraw = true;
	}

	public void setPixel(int x, int y, int color) {
		if(x < 0 || x >= width || y < 0 || y >= height) {
			return;
		}

		canvas[x][y] = color;
		needsRedraw = true;
	}

	public int getPixel(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) {
			return 0x00000000; // Return transparent black for out-of-bounds
		}
		return canvas[x][y];
	}

	private void updateImage() {
		NativeImage image = new NativeImage(width, height, false);
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int argb = canvas[x][y];
				int abgr = (argb & 0xFF000000) | ((argb & 0x00FF0000) >> 16) | (argb & 0x0000FF00) | ((argb & 0x000000FF) << 16);

				image.setPixelRGBA(x, y, abgr);
			}
		}

		var optTexture = DynamicImageResources.uploadImage("canvas_" + canvasId, image);
		renderedTexture = optTexture.orElse(null);
		needsRedraw = false;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		if(needsRedraw) {
			updateImage();
		}
		if(renderedTexture == null) {
			return;
		}

		pGuiGraphics.pose().popPose();
		//pGuiGraphics.blit(this.renderedTexture.resource(), 0, 0, 0, 0, this.width, this.height);
		RenderSystem.enableBlend();
		//		var oldMin = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
		//		var oldMag = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
		//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		pGuiGraphics.blitInscribed(this.renderedTexture.resource(), 0, 0, this.width, this.height, this.width, this.height, true, true);
		//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, oldMin);
		//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, oldMag);

		RenderSystem.disableBlend();
		pGuiGraphics.pose().pushPose();
	}
}
