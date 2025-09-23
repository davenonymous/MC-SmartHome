package com.davenonymous.smarthome.gui.home.sidebar;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class SidebarBadge extends WidgetTextBox {
	int badgeColor;

	public SidebarBadge(String text, int badgeColor, int textColor) {
		super(text, textColor);
		this.setFont(ModFonts.TINY);
		this.badgeColor = badgeColor;
		this.autoWidth(32);
		this.setWidth(this.width + 6);
		this.setHeight(13);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		var tint = ColorHelper.toVector(badgeColor);
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(tint.x(), tint.y(), tint.z(), tint.w());
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R2), 0, 0, this.width, this.height);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.disableBlend();
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(4, 3.5, 0);
		super.draw(guiGraphics, window);
		guiGraphics.pose().popPose();
	}
}
