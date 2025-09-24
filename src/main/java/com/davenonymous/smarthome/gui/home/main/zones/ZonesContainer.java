package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
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
	private ZoneRendererWidget zoneDisplay;
	private ZoneDetailWidget zoneDetail;

	private WidgetVBox zoneButtons;
	private WidgetHBox newZoneButtons;

	public ZonesContainer() {
		zoneButtons = new WidgetVBox();
		this.add(zoneButtons);

		zoneDetail = new ZoneDetailWidget();
		this.add(zoneDetail);

		newZoneButtons = new WidgetHBox();
		this.add(newZoneButtons);

		zoneDisplay = new ZoneRendererWidget();
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
			if(zone.isDeleted()) {
				continue;
			}
			var button = new WidgetTextBox(zone.name(), ChatFormatting.WHITE.getColor());
			button.setFont(ModFonts.BASEL);
			button.autoWidth();
			button.autoHeight();
			button.addListener(MouseEnterEvent.class, (event, widget) -> {
				zoneDisplay.selectedZone = zone.name();
				button.setTextColor(ChatFormatting.YELLOW.getColor());
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
			button.addListener(MouseExitEvent.class, (event, widget) -> {
				zoneDisplay.selectedZone = null;
				button.setTextColor(ChatFormatting.WHITE.getColor());
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
		var player = Minecraft.getInstance().player;
		var itemStream = player.getInventory().items.stream().limit(9);

		var rangerFinderDataComponents = itemStream
			.filter(stack -> !stack.isEmpty() && stack.has(ModDataComponents.RANGER_FINDER_DATA_COMPONENT))
			.map(stack -> stack.get(ModDataComponents.RANGER_FINDER_DATA_COMPONENT))
			.distinct()
			.toList();

		for(var rangeFinderData : rangerFinderDataComponents) {
			Widget button;

			if(rangeFinderData.toAABB() != null && selectedHome.getZoneCrossing(rangeFinderData.toAABB()) != null) {
				button = new InvalidZoneWidget(this, rangeFinderData);
			} else {
				button = new AddZoneButtonWidget(this, rangeFinderData);
			}

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

		zoneDisplay.setDimensions(displayX, 5, displayWidth, this.height - 55);
		zoneButtons.setDimensions(displayX, 5, 100, this.height - 50);
		zoneDetail.setDimensions(detailX, 5, detailWidth, this.height - 10);

		int newZoneButtonsWidth = Math.max(newZoneButtons.width(), displayWidth);
		newZoneButtons.setDimensions(displayX, this.height - 42, newZoneButtonsWidth, 32);
	}
}
