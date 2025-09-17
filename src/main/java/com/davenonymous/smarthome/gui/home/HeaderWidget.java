package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class HeaderWidget extends WidgetHBox {
	WidgetTextBox titleText;

	public HeaderWidget() {
		this.setSpacing(2);
		this.setPadding(0);

		this.titleText = new WidgetTextBox(I18n.get("smarthome.gui.home.title"));
		this.titleText.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.addContentBox(this.titleText, FlexAlign.START);
	}

	public void updateWidgetSizes() {
		this.titleText.autoWidth();
		this.titleText.setHeight(10);
	}
}
