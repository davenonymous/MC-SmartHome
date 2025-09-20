package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

public class ZonesContainer extends WidgetPanel {
	private ZonesWidget zoneDisplay;
	private ZoneDetailWidget zoneDetail;

	private WidgetVBox zoneButtons;

	public ZonesContainer() {
		zoneButtons = new WidgetVBox();
		this.add(zoneButtons);

		zoneDetail = new ZoneDetailWidget();
		this.add(zoneDetail);

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
			button.addListener(MouseClickEvent.class, (event, widget) -> {
				if(zoneDetail.selectedZone() == zone) {
					zoneDetail.setSelectedZone(null);
					return WidgetEventResult.HANDLED;
				}
				zoneDetail.setSelectedZone(zone);
				return WidgetEventResult.HANDLED;
			});

			zoneButtons.addContentBox(button, FlexSizer.FlexAlign.START);
		}

	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		int displayWidth = (int)(this.width() * 2 / 3f);
		int detailWidth = this.width() - displayWidth - 15;
		int displayX = 5;
		int detailX = displayX + displayWidth + 10;

		zoneDisplay.setDimensions(displayX, 5, displayWidth, this.height - 10);
		zoneButtons.setDimensions(displayX, 5, 100, this.height - 20);
		zoneDetail.setDimensions(detailX, 5, detailWidth, this.height - 10);
	}
}
