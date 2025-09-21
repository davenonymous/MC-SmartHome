package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.lib.gui.theme.Vanilla;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;

public class RangeFinderHud extends WidgetPanel {
	public RangeFinderHud(int screenWidth, int screenHeight, RangerFinderDataComponent rangeFinderData) {
		super();
		int xPadding = 15;
		int yPadding = 15;
		float alpha = 0.5f;
		int alphaInt = ((int)(alpha * 255) << 24);

		WidgetSprite leftClick = new WidgetSprite(Vanilla.leftClick).setColor(0xFFFFFF | alphaInt);
		leftClick.setPosition((screenWidth - leftClick.width()) / 2 - xPadding, (screenHeight - leftClick.height()) / 2 + yPadding);

		if(rangeFinderData != null) {
			if(rangeFinderData.A() != null) {
				WidgetTextBox aText = new WidgetTextBox(rangeFinderData.A().toShortString());
				aText.setFont(ModFonts.TINY);
				aText.autoWidth();
				aText.setPosition((screenWidth) / 2 - xPadding - aText.width(), (screenHeight - aText.height()) / 2 + yPadding + 20);
				aText.setTextColor(0xAAAAAA | alphaInt);
				this.add(aText);
			}

			if(rangeFinderData.B() != null) {
				WidgetTextBox bText = new WidgetTextBox(rangeFinderData.B().toShortString());
				bText.setFont(ModFonts.TINY);
				bText.autoWidth();
				bText.setPosition((screenWidth) / 2 + xPadding, (screenHeight - bText.height()) / 2 + yPadding + 20);
				bText.setTextColor(0xAAAAAA | alphaInt);
				this.add(bText);
			}

			if(rangeFinderData.A() != null || rangeFinderData.B() != null) {
				String sizeText = String.format(
					"%dx%dx%d",
					Math.abs(rangeFinderData.A().getX() - rangeFinderData.B().getX()) + 1,
					Math.abs(rangeFinderData.A().getY() - rangeFinderData.B().getY()) + 1,
					Math.abs(rangeFinderData.A().getZ() - rangeFinderData.B().getZ()) + 1
				);
				WidgetTextBox sizeBox = new WidgetTextBox(sizeText);
				sizeBox.autoWidth();
				sizeBox.setPosition((screenWidth - sizeBox.width()) / 2 + 3, (screenHeight - sizeBox.height()) / 2 + yPadding + 40);
				sizeBox.setTextColor(0xFFFFFF | alphaInt);
				this.add(sizeBox);
			}
		}

		var player = Minecraft.getInstance().player;
		BlockHitResult blockHit = RangeFinderItem.getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.ANY, 48.0f);
		if(blockHit.getType() == BlockHitResult.Type.BLOCK) {
			var distance = Math.sqrt(blockHit.distanceTo(player));

			WidgetTextBox centerText = new WidgetTextBox(String.format("%.1fm", distance));
			centerText.autoWidth();
			centerText.setPosition((screenWidth - centerText.width()) / 2 + 3, (screenHeight - centerText.height()) / 2 - yPadding);
			this.add(centerText);
			centerText.setTextColor(0xAAAAAA | alphaInt);
		}


		WidgetSprite rightClick = new WidgetSprite(Vanilla.rightClick).setColor(0xFFFFFF | alphaInt);
		rightClick.setPosition((screenWidth - rightClick.width()) / 2 + xPadding, (screenHeight - rightClick.height()) / 2 + yPadding);

		this.add(leftClick);
		this.add(rightClick);
	}
}
