package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;

public class ZonesContainer extends WidgetPanel {
	private ZonesWidget zoneDisplay;
	private ZoneDetailWidget zoneDetail;

	private WidgetVBox zoneButtons;
	private WidgetHBox newZoneButtons;

	public ZonesContainer() {
		zoneButtons = new WidgetVBox();
		this.add(zoneButtons);

		zoneDetail = new ZoneDetailWidget();
		this.add(zoneDetail);

		zoneDisplay = new ZonesWidget();
		this.add(zoneDisplay);

		newZoneButtons = new WidgetHBox();
		this.add(newZoneButtons);

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
				if(zoneDetail.selectedZone() != null && zoneDetail.selectedZone().id().equals(zone.id())) {
					zoneDetail.setSelectedZone(null);
					return WidgetEventResult.HANDLED;
				}
				zoneDetail.setSelectedZone(zone);
				return WidgetEventResult.HANDLED;
			});

			zoneButtons.addContentBox(button, FlexSizer.FlexAlign.START);
		}

		newZoneButtons.clear();
		var rangerFinderDataComponents = Minecraft.getInstance().player.inventoryMenu.getItems().stream()
			.filter(stack -> !stack.isEmpty() && stack.has(ModDataComponents.RANGER_FINDER_DATA_COMPONENT))
			.map(stack -> stack.get(ModDataComponents.RANGER_FINDER_DATA_COMPONENT))
			.filter(data -> data.toAABB() != null && selectedHome.getZoneCrossing(data.toAABB()) == null)
			.distinct()
			.toList();

		for(var rangeFinderData : rangerFinderDataComponents) {
			var button = new AddZoneButtonWidget(this, rangeFinderData);

			button.addListener(MouseEnterEvent.class, (event, widget) -> {
				zoneDisplay.selectedRangeFinder = rangeFinderData;
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
			button.addListener(MouseExitEvent.class, (event, widget) -> {
				zoneDisplay.selectedRangeFinder = null;
				return WidgetEventResult.CONTINUE_PROCESSING;
			});

			newZoneButtons.addContentBox(button, FlexSizer.FlexAlign.START);
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

		int newZoneButtonsWidth = Math.max(newZoneButtons.width(), displayWidth);
		newZoneButtons.setPosition(displayX, this.height - 42);
		newZoneButtons.setWidth(newZoneButtonsWidth);
		newZoneButtons.setHeight(32);
	}
}
