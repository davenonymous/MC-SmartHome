package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.CircularPointedArrayList;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class HeaderWidget extends WidgetHBox {
	WidgetTextBox titleText;


	public HeaderWidget() {
		this.setSpacing(2);
		this.setPadding(0);

		this.titleText = new WidgetTextBox("The quick brown fox jumps over the lazy dog"); //I18n.get("smarthome.gui.home.title"));
		this.titleText.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.titleText.setWordWrap(true);
		this.titleText.setStyle(style -> style
			.withFont(SmartHome.resource("pixel-ascii"))
			.withColor(ChatFormatting.DARK_GRAY)
		);
		this.addContentBox(this.titleText, FlexAlign.START);
	}

	public void updateWidgetSizes() {
		//this.titleText.autoWidth();
		this.titleText.autoWidth();
		this.titleText.setHeight(20);
	}
}
