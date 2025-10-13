package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.Animations;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

public class LoadingWidget extends WidgetPanel {
	public LoadingWidget(int width, int height) {
		super();
		this.setSize(width, height);

		var spinnerSprite = new WidgetSprite(HackerNoon.Regular.spinner);
		spinnerSprite.setSize(48, 48);
		spinnerSprite.setPosition((width - 48) / 2, (height - 48) / 2);
		spinnerSprite.addAnimation(Animations.spin(true, 1));
		this.add(spinnerSprite);
	}


	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(isInWorld()) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(0, 0, 2);
			RenderSystem.setShaderColor(0.6f, 0.8f, 1, .8f);
		}
		RenderSystem.enableBlend();
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);

		if(isInWorld()) {
			guiGraphics.pose().translate(0, 0, -2);
		}
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		if(isInWorld()) {
			guiGraphics.pose().translate(0, 0, -2);
			RenderSystem.setShaderColor(1, 1, 1, 1f);
		}

		super.draw(guiGraphics, window);

		if(isInWorld()) {
			guiGraphics.pose().popPose();
		}
	}
}
