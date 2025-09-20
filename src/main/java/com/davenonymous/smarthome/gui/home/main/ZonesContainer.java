package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseEnterEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseExitEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

public class ZonesContainer extends WidgetPanel {
	private ZonesWidget zoneDisplay;

	private WidgetVBox zoneButtons;

	public ZonesContainer() {
		zoneButtons = new WidgetVBox();
		this.add(zoneButtons);

		zoneDisplay = new ZonesWidget();
		this.add(zoneDisplay);


		this.addListener(
			GuiDataUpdatedEvent.class, (event, widget) -> {
				refreshZoneList();
				return WidgetEventResult.CONTINUE_PROCESSING;
			});

		updateWidgetSizes();
		refreshZoneList();
	}

	public void refreshZoneList() {
		zoneDisplay.refreshZoneList();

		if(HomeScreen.get() == null) {
			return;
		}

		var selectedHome = HomeScreen.get().selectedHome;
		if(selectedHome == null) {
			return;
		}

		if(selectedHome.zones().isEmpty()) {
			return;
		}

		zoneButtons.clear();
		for(var zone : selectedHome.zones()) {
			var button = new WidgetTextBox(zone.name(), ChatFormatting.WHITE.getColor());
			button.setFont(ModFonts.BASEL);
			button.autoWidth();
			button.autoHeight();
			button.addListener(MouseEnterEvent.class, (event, widget) -> {
				zoneDisplay.selectedZone = zone.name();
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
			button.addListener(MouseExitEvent.class, (event, widget) -> {
				zoneDisplay.selectedZone = null;
				return WidgetEventResult.CONTINUE_PROCESSING;
			});

			zoneButtons.addContentBox(button, FlexSizer.FlexAlign.START);
		}

	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		zoneDisplay.setDimensions(5, 5, this.width() - 10, this.height - 10);
		zoneButtons.setDimensions(5, 5, 100, this.height - 20);
	}
}
