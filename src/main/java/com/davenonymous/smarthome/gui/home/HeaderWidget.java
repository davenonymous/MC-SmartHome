package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class HeaderWidget extends WidgetHBox {
	WidgetTextBox titleText;
	HomeSelectWidget homeSelect;

	public HeaderWidget(HomeScreen homeScreen) {
		this.setSpacing(2);
		this.setPadding(0);
		this.setHeight(20);

		this.titleText = new WidgetTextBox(HomeScreen.TITLE.get());
		this.titleText.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.titleText.setWordWrap(true);
		this.addFlexBox(this.titleText, FlexAlign.CENTER, 1);

		this.addFlexBox(new Widget(), FlexAlign.CENTER, 1);

		this.homeSelect = new HomeSelectWidget();
		this.addFlexBox(this.homeSelect, FlexAlign.CENTER, 1);

	}

	public void updateWidgetSizes() {
		//this.titleText.autoWidth();
		this.titleText.autoWidth(155);
		this.titleText.autoHeight();
		this.homeSelect.updateWidgetSizes();
	}
}
