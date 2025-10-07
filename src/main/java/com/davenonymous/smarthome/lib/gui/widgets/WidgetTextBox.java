package com.davenonymous.smarthome.lib.gui.widgets;


import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.util.function.Function;

public class WidgetTextBox extends Widget {
	private String text;
	private int textColor = 0xFFFFFFF;
	private int hoverColor = 0xFFFFFFF;
	private boolean dropShadow = false;
	private boolean wordWrap = false;
	protected Style style = Style.EMPTY;
	private ModFonts.FontSpec font;

	public WidgetTextBox(String text) {
		this(text, 0xFFFFFFFF, 0xFFFFFFFF);
	}

	public WidgetTextBox(String text, int textColor) {
		this(text, textColor, textColor);
	}

	public WidgetTextBox(String text, int textColor, int hoverColor) {
		super();
		this.text = text;
		this.textColor = textColor;
		this.hoverColor = hoverColor;
		this.setWidth(100);
		this.setHeight(9);
	}

	public void autoHeight() {
		int lineHeight = 9;
		if(font != null) {
			lineHeight = font.lineHeight();
		}

		int lineWidth = wordWrap ? width : Integer.MAX_VALUE;
		int guessedHeight = GUIHelper.wordWrapHeight(Minecraft.getInstance().font, text, style, lineWidth, (int)(lineHeight * scale));
		if(guessedHeight < 1) {
			guessedHeight = lineHeight;
		}

		this.setHeight(Math.round(guessedHeight * scale));
	}

	public void autoWidth() {
		this.autoWidth(wordWrap ? width : Integer.MAX_VALUE);
	}

	public void autoWidth(int maxWidth) {
		var guessedWidth = GUIHelper.longestWrappedLine(Minecraft.getInstance().font, FormattedText.of(text, style), style, (int)(maxWidth / scale)) + 2;
		if(guessedWidth < 1) {
			guessedWidth = 2;
		}
		this.setWidth(Math.round(guessedWidth * scale));
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
		if(this.textColor == hoverColor) {
			this.hoverColor = textColor;
		}
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
	public void renderExtraDebugInfo(GuiGraphics pGuiGraphics, Window window) {
		if(this.font != null) {
			var mcFont = Minecraft.getInstance().font;
			String fontName = "Font: " + this.font.id().getPath();
			pGuiGraphics.drawString(mcFont, fontName, 0, 30, 0xFF8000);

			String visibleWidth = "Line Height: " + this.font.lineHeight();
			pGuiGraphics.drawString(mcFont, visibleWidth, 0, 40, 0xFF8000);

			String wrodWrapInfo = "Word Wrap: " + (this.wordWrap ? "ON" : "OFF");
			pGuiGraphics.drawString(mcFont, wrodWrapInfo, 0, 50, 0xFF8000);

			if(this.wordWrap && this.style != null) {
				int lines = GUIHelper.wordWrapLines(mcFont, this.text, this.style, Math.round(this.width / scale));
				String linesInfo = "Lines: " + lines;
				pGuiGraphics.drawString(mcFont, linesInfo, 0, 60, 0xFF8000);
			}
		}
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		if(text == null) {
			return;
		}

		pGuiGraphics.pose().pushPose();
		RenderSystem.enableBlend();

		int lineHeight = 9;
		int yOffset = 0;
		if(font != null) {
			lineHeight = font.lineHeight();
			yOffset = font.yOffset();
		}

		int color = textColor;
		if(isHovered()) {
			color = hoverColor;
		}

		int lineWidth = wordWrap ? Math.round(width / scale) : Integer.MAX_VALUE;
		pGuiGraphics.enableScissor(getActualX(), getActualY(), getActualX() + (int)(width / scale), getActualY() + (int)(height / scale));
		GUIHelper.drawWordWrap(pGuiGraphics, Minecraft.getInstance().font, FormattedText.of(text, style), style, 0, -yOffset, lineWidth, lineHeight, color, dropShadow);
		pGuiGraphics.disableScissor();

		RenderSystem.disableBlend();
		pGuiGraphics.pose().popPose();
	}
}
