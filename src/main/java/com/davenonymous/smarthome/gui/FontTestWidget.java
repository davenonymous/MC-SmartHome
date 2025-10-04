package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

import java.util.List;

public class FontTestWidget extends WidgetPanel {

	public FontTestWidget() {
		var fonts = List.of(
//			ModFonts.BASEL,
//			ModFonts.DOS,
//			ModFonts.MONKEY_FILLED,
//			ModFonts.MONKEY_OUTLINE,
//			ModFonts.NANO,
//			ModFonts.NOKIA,
//			ModFonts.PIXEL,
//			ModFonts.TINY,
//			ModFonts.WENDY,
			new ModFonts.FontSpec(SmartHome.resource("nokia2-ascii"), 14, 16),
			new ModFonts.FontSpec(SmartHome.resource("nokia3-ascii"), 12, 22),
			new ModFonts.FontSpec(SmartHome.resource("samsung-ascii"), 6, 13),
			new ModFonts.FontSpec(SmartHome.resource("receipt-ascii"), 18, 16)
		);

		this.width = 500;
		this.height = 20 + fonts.size() * 40;

		int xOffset = 10;
		int yOffset = 10;

		int wrapWidth = 300;
		var vanillaBox = new WidgetTextBox("Amazingly few discothèques provide jukeboxes. äöüß ж 01 234567890 3.14 A, B, C");
		vanillaBox.setPosition(xOffset, yOffset);
		vanillaBox.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		vanillaBox.setWordWrap(true);
		vanillaBox.autoWidth(wrapWidth);
		vanillaBox.autoHeight();
		yOffset += vanillaBox.height + 5;
		this.add(vanillaBox);

		for(var font : fonts) {
			var textBox = new WidgetTextBox(font.id().getPath().replaceAll("-.*$", "") + ": Amazingly, few discothèques provide jukeboxes. äöüß 01234567890 ?!#@%$§\" 3.14 A, B, C");
			textBox.setPosition(xOffset, yOffset);
			textBox.setFont(font);
			textBox.setTextColor(ChatFormatting.DARK_GRAY.getColor());
			textBox.setWordWrap(true);
			textBox.scale = 2/3f;
			textBox.autoWidth(wrapWidth);
			textBox.autoHeight();

			yOffset += textBox.height + 5;
			this.add(textBox);
		}

		this.setWidth(300);
		this.setHeight(yOffset + 10);
	}
}
