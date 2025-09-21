package com.davenonymous.smarthome.gui.home.main;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.networking.actions.SetZoneNamePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.neoforge.network.PacketDistributor;

public class ZoneDetailWidget extends WidgetVBox {
	private HomeZone selectedZone;

	private StringInputWidget zoneRenameInput;
	private WidgetTextBox zoneSize;
	private WidgetTextBox zoneDevices;

	public ZoneDetailWidget() {
		this.setPadding(8);
		this.setSpacing(4);

		zoneRenameInput = new StringInputWidget("", "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		zoneRenameInput.setDrawBackground(false);
		zoneRenameInput.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
		zoneRenameInput.addListener(ValueChangedEvent.class, (event, widget) -> {
			if(selectedZone == null) {
				return WidgetEventResult.HANDLED;
			}
			updateWidgetSizes();

			if(zoneRenameInput.getValue().isEmpty()) {
				return WidgetEventResult.HANDLED;
			}
			PacketDistributor.sendToServer(new SetZoneNamePayload(selectedZone.home().serverLocation(), selectedZone.home().id(), selectedZone.id(), zoneRenameInput.getValue()));
			return WidgetEventResult.HANDLED;
		});
		this.addContentBox(zoneRenameInput, FlexAlign.CENTER);

		zoneSize = new WidgetTextBox("", ChatFormatting.GRAY.getColor());
		zoneSize.setFont(ModFonts.TINY);
		this.addContentBox(zoneSize, FlexAlign.CENTER);

		zoneDevices = new WidgetTextBox("Devices", ChatFormatting.WHITE.getColor());
		this.addContentBox(zoneDevices, FlexAlign.START);
	}

	public ZoneDetailWidget setSelectedZone(HomeZone selectedZone) {
		this.selectedZone = selectedZone;
		if(selectedZone == null) {
			zoneSize.setText("");
			zoneRenameInput.setValue("");
		} else {
			zoneRenameInput.setValue(selectedZone.name());
			zoneRenameInput.nativeWidget().moveCursorToStart(false);
			var bounds = selectedZone.bounds();
			int sizeX = (int)Math.round(bounds.maxX - bounds.minX);
			int sizeY = (int)Math.round(bounds.maxY - bounds.minY);
			int sizeZ = (int)Math.round(bounds.maxZ - bounds.minZ);
			zoneSize.setText(String.format("%dx%dx%d", sizeX, sizeY, sizeZ));
		}

		updateWidgetSizes();
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		zoneRenameInput.autoWidth();
		zoneRenameInput.setHeight(12);
		zoneSize.autoWidth();
		zoneSize.autoHeight();
		zoneDevices.autoWidth();
		zoneDevices.autoHeight();
		this.update(null);
	}

	public HomeZone selectedZone() {
		return selectedZone;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		if(selectedZone == null) {
			return;
		}

		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		guiGraphics.fill(3, 3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}
}
