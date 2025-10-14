package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.data.TimeRangeEnum;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.networking.actions.SetTimeRangePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.network.PacketDistributor;

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

		if(dashboardScreen.selectedHome != null) {
			this.timeRangeWidget = new TimeRangeWidget(dashboardScreen.selectedHome.timeRange());
			this.timeRangeWidget.addListener(
				ValueChangedEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(new SetTimeRangePayload(dashboardScreen.selectedHome.id(), (TimeRangeEnum) event.newValue));
					return WidgetEventResult.CONTINUE_PROCESSING;
				});
			this.addContentBox(this.timeRangeWidget, FlexAlign.CENTER);
		}

		this.addFlexBox(new Spacer(1, 1), FlexAlign.CENTER, 1);

		this.homeSelect = new HomeSelectWidget(dashboardScreen.getMenu().ownedHomes, dashboardScreen.selectedHome);
		this.addFlexBox(this.homeSelect, FlexAlign.CENTER, 1);

	}

	public void updateWidgetSizes() {
		//this.titleText.autoWidth();
		this.titleText.autoWidth(155);
		this.titleText.autoHeight();
		this.homeSelect.updateWidgetSizes();
	}
}
