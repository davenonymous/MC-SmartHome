package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

public class HeaderWidget extends WidgetHBox {
	WidgetTextBox titleText;
	HomeSelectWidget homeSelect;
	TimeRangeWidget timeRangeWidget;

	public HeaderWidget(DashboardScreen dashboardScreen) {
		this.setSpacing(2);
		this.setPadding(0);
		this.setHeight(20);

		this.titleText = new WidgetTextBox(DashboardScreen.TITLE.get());
		this.titleText.setFont(ModFonts.NOKIA3);
		this.titleText.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.titleText.setWordWrap(true);
		this.addFlexBox(this.titleText, FlexAlign.CENTER, 1);

		this.addFlexBox(new Spacer(1, 1), FlexAlign.CENTER, 1);

		this.timeRangeWidget = new TimeRangeWidget();
		this.addFlexBox(this.timeRangeWidget, FlexAlign.CENTER, 1);

		this.addFlexBox(new Spacer(1, 1), FlexAlign.CENTER, 1);

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
