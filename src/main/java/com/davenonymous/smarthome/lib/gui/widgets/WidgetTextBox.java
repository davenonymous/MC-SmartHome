package com.davenonymous.smarthome.lib.gui.widgets;


import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class WidgetTextBox extends Widget {
	private String text;
	private int textColor = 0xFFFFFF;
	private boolean dropShadow = false;
	private boolean wordWrap = false;
	protected Style style = Style.EMPTY;
	private ModFonts.FontSpec font;

	public WidgetTextBox(String text) {
		this.text = text;
		this.setWidth(100);
		this.setHeight(9);
	}

	public WidgetTextBox(String text, int textColor) {
		this.text = text;
		this.textColor = textColor;
		this.setWidth(100);
		this.setHeight(9);
	}

	public void autoHeight() {
		int lineHeight = 9;
		if(font != null) {
			lineHeight = font.lineHeight();
		}

		int lineWidth = wordWrap ? width : Integer.MAX_VALUE;
		int guessedHeight = GUIHelper.wordWrapHeight(Minecraft.getInstance().font, text, style, lineWidth, lineHeight);
		if(guessedHeight < 1) {
			guessedHeight = lineHeight;
		}
		this.setHeight(guessedHeight);
	}

	public void autoWidth() {
		this.autoWidth(wordWrap ? width : Integer.MAX_VALUE);
	}

	public void autoWidth(int maxWidth) {
		var guessedWidth = GUIHelper.longestWrappedLine(Minecraft.getInstance().font, FormattedText.of(text, style), maxWidth) + 2;
		if(guessedWidth < 1) {
			guessedWidth = 2;
		}
		this.setWidth(guessedWidth );
	}

	public WidgetTextBox setStyle(Function<Style, Style> style) {
		this.style = style.apply(this.style);
		return this;
	}

	public WidgetTextBox setFont(ModFonts.FontSpec font) {
		this.font = font;
		this.setStyle(style -> style.withFont(font.id()));
		return this;
	}

	public boolean isWordWrap() {
		return wordWrap;
	}

	public WidgetTextBox setWordWrap(boolean wordWrap) {
		this.wordWrap = wordWrap;
		return this;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}

	public boolean getDropShadow() {
		return dropShadow;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Screen screen) {
		if(text == null) {
			return;
		}

		pGuiGraphics.pose().pushPose();
		RenderSystem.enableBlend();

		int scale = (int)screen.getMinecraft().getWindow().getGuiScale();
		int bottomOffset = (int) (((double) (screen.getMinecraft().getWindow().getHeight() / scale) - (getActualY() + height)) * scale);
		int heightTmp = (height * scale) - 1;
		if(heightTmp < 0) {
			heightTmp = 0;
		}

		int lineHeight = 9;
		int yOffset = 0;
		if(font != null) {
			lineHeight = font.lineHeight();
			yOffset = font.yOffset();
		}

		int lineWidth = wordWrap ? width : Integer.MAX_VALUE;
		RenderSystem.enableScissor(getActualX() * scale - 3, bottomOffset + 2, width * scale, heightTmp);
		GUIHelper.drawWordWrap(pGuiGraphics, screen.getMinecraft().font, FormattedText.of(text, style), 0, -yOffset, lineWidth, lineHeight, textColor);
		RenderSystem.disableScissor();

		RenderSystem.disableBlend();
		pGuiGraphics.pose().popPose();
	}
}
