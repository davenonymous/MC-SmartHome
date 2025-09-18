package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

import java.util.List;

public class FontTestWidget extends WidgetVBox {

	public FontTestWidget() {
		var fonts = List.of(
			ModFonts.BASEL,
			ModFonts.DOS,
			ModFonts.MONKEY_FILLED,
			ModFonts.MONKEY_OUTLINE,
			ModFonts.NANO,
			ModFonts.NOKIA,
			ModFonts.PIXEL,
			ModFonts.TINY,
			ModFonts.WENDY
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
			var textBox = new WidgetTextBox(font.id().getPath().replaceAll("-.*$", "") + ": The quick brown fox jumps over the lazy dog 01234567890 3.14 A, B, C");
			textBox.setTextColor(ChatFormatting.DARK_GRAY.getColor());
			textBox.setWidth(this.width);
			textBox.setWordWrap(true);
			textBox.setFont(font);
			textBox.autoWidth();
			textBox.autoHeight();
			this.addContentBox(textBox, FlexAlign.START);
		}

	}
}
