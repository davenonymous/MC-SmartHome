package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.CircularPointedArrayList;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class FontTestWidget extends WidgetVBox {

	public FontTestWidget() {
		var fonts = List.of(
			SmartHome.resource("basel-ascii"),
			SmartHome.resource("nano-ascii"),
			SmartHome.resource("pixel-ascii"),
			SmartHome.resource("tiny-ascii"),
			SmartHome.resource("wendy-ascii"),
			SmartHome.resource("dos-ascii"),
			SmartHome.resource("nokia-ascii"),
			SmartHome.resource("monkey-ascii"),
			SmartHome.resource("monkey_outline-ascii")

		);

		this.setSpacing(2);
		this.setPadding(0);
		this.width = 500;
		this.height = 20 + fonts.size() * 24;

		var vanillaBox = new WidgetTextBox("The quick brown fox jumps over the lazy dog");
		vanillaBox.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		vanillaBox.setWordWrap(true);
		vanillaBox.setStyle(style -> style
			.withColor(ChatFormatting.DARK_GRAY)
		);
		vanillaBox.autoWidth(this.width);
		vanillaBox.setHeight(24);
		this.addContentBox(vanillaBox, FlexAlign.START);

		for(var font : fonts) {
			var textBox = new WidgetTextBox(font.getPath().replaceAll("-.*$", "") + ": The quick brown fox jumps over the lazy dog");
			textBox.setTextColor(ChatFormatting.DARK_GRAY.getColor());
			textBox.setWordWrap(true);
			textBox.setStyle(style -> style
				.withFont(font)
				.withColor(ChatFormatting.DARK_GRAY)
			);
			textBox.autoWidth();
			textBox.setHeight(24);
			this.addContentBox(textBox, FlexAlign.START);
		}

	}
}
