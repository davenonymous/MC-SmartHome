package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class HeaderWidget extends WidgetHBox {
	WidgetTextBox titleText;

	public HeaderWidget(HomeScreen homeScreen) {
		this.setSpacing(2);
		this.setPadding(0);

		this.titleText = new WidgetTextBox(I18n.get("smarthome.gui.home.title"));
		this.titleText.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.titleText.setWordWrap(true);
		this.titleText.setFont(ModFonts.NOKIA);
		this.addContentBox(this.titleText, FlexAlign.START);


	}

	public void updateWidgetSizes() {
		//this.titleText.autoWidth();
		this.titleText.autoWidth(155);
		this.titleText.autoHeight();
	}
}
