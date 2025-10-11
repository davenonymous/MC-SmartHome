package com.davenonymous.smarthome.lib.gui.configurable;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GUIHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BetterEditBox extends EditBox {
	private int yTextOffset;
	private Consumer<Boolean> focusResponder;

	public BetterEditBox(Font font, int width, int height, Component message) {
		super(font, width, height, message);
	}

	public BetterEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component message) {
		super(font, x, y, width, height, editBox, message);
	}

	public BetterEditBox(Font font, int x, int y, int width, int height, Component message) {
		super(font, x, y, width, height, message);
	}

	public void setFocusResponder(Consumer<Boolean> focusResponder) {
		this.focusResponder = focusResponder;
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		if(this.focusResponder != null) {
			this.focusResponder.accept(focused);
		}
	}

	@Override
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		if (this.isVisible()) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0.0F, -(float)this.yTextOffset, 0.0F);

			var font = Minecraft.getInstance().font;
			if (this.isBordered()) {
				ResourceLocation resourcelocation = SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_BORDER);
				guiGraphics.blitSprite(resourcelocation, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			}

			int textColor = this.isEditable ? this.textColor : this.textColorUneditable;
			int i = this.cursorPos - this.displayPos;

			String s = font.plainSubstrByWidth(this.value.substring(this.displayPos), this.getInnerWidth());
			boolean flag = i >= 0 && i <= s.length();
			boolean flag1 = this.isFocused() && (Util.getMillis() - this.focusedTime) / 300L % 2L == 0L && flag;
			int j = this.bordered ? this.getX() + 4 : this.getX();
			int k = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
			int l = j;
			int i1 = Mth.clamp(this.highlightPos - this.displayPos, 0, s.length());
			if (!s.isEmpty()) {
				String s1 = flag ? s.substring(0, i) : s;
				l = guiGraphics.drawString(font, this.formatter.apply(s1, this.displayPos), j, k, textColor, this.textShadow);
			}

			boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.maxLength;
			int j1 = l;
			if (!flag) {
				j1 = i > 0 ? j + this.width : j;
			} else if (flag2) {
				j1 = l - 1;
				//l--;
			}

			if (!s.isEmpty() && flag && i < s.length()) {
				guiGraphics.drawString(font, this.formatter.apply(s.substring(i), this.cursorPos), l, k, textColor, this.textShadow);
			}

			if (this.hint != null && s.isEmpty() && !this.isFocused()) {
				guiGraphics.drawString(font, this.hint, l, k, textColor, this.textShadow);
			}

			if (!flag2 && this.suggestion != null) {
				guiGraphics.drawString(font, this.suggestion, j1 - 1, k, -8355712, this.textShadow);
			}

			guiGraphics.pose().popPose();
			if (flag1) {
				if (flag2) {
					guiGraphics.fill(RenderType.guiOverlay(), j1, k - 1, j1 + 1, k + 1 + 9, textColor | 0xFF000000);
				} else {
					guiGraphics.drawString(font, "_", j1, k, textColor, this.textShadow);
				}
			}

			if (i1 != i) {
				int k1 = j + font.width(s.substring(0, i1));
				this.renderHighlight(guiGraphics, j1, k - 1, k1 - 1, k + 1 + 9);
			}


		}
	}

	private void renderHighlight(GuiGraphics guiGraphics, int minX, int minY, int maxX, int maxY) {
		if (minX < maxX) {
			int i = minX;
			minX = maxX;
			maxX = i;
		}

		if (minY < maxY) {
			int j = minY;
			minY = maxY;
			maxY = j;
		}

		if (maxX > this.getX() + this.width) {
			maxX = this.getX() + this.width;
		}

		if (minX > this.getX() + this.width) {
			minX = this.getX() + this.width;
		}

		guiGraphics.fill(RenderType.guiTextHighlight(), minX, minY, maxX, maxY, -16776961);
	}

	public void setYTextOffset(int yTextOffset) {
		this.yTextOffset = yTextOffset;
	}

	public int getYTextOffset() {
		return yTextOffset;
	}
}
