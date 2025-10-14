package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.general.TrashButton;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.zones.MarkZoneAsDeletedPayload;
import com.davenonymous.smarthome.networking.actions.zones.SetZoneNamePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.network.PacketDistributor;

public class ZoneDetailWidget extends WidgetVBox {
	private HomeZone selectedZone;

	private StringInputWidget zoneRenameInput;
	private WidgetTextBox zoneSize;
	private WidgetTextBox zoneDeviceLabel;
	private WidgetSprite deleteIcon;
	private WidgetVBox devicesList;
	private ZoneSizeEditor zoneSizeEditor;

	@I18DataGen(lang = "en_us", string = "Click to rename")
	@I18DataGen(lang = "de_de", string = "Klicken zum Umbenennen")
	public static final I18String RENAMABLE_ZONE = I18String.gui("home.zones", "detail.renameable");

	@I18DataGen(lang = "en_us", string = "Devices")
	@I18DataGen(lang = "de_de", string = "Geräte")
	public static final I18String DEVICES_LABEL = I18String.gui("home.zones", "detail.devices");

	@I18DataGen(lang = "en_us", string = "Hold Ctrl + Shift and click to delete this zone")
	@I18DataGen(lang = "de_de", string = "Halte Strg + Shift und klicke, um diese Zone zu löschen")
	public static final I18String DELETE_ZONE = I18String.gui("home.zones", "detail.delete");


	private boolean isEditingZoneSize = false;

	public ZoneDetailWidget() {
		this.setPadding(8);
		this.setSpacing(4);

		zoneRenameInput = new StringInputWidget("", ModFonts.SAFE_FONT_CHARS);
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
		zoneRenameInput.setTooltipElements(WrappedStringTooltipComponent.orange(RENAMABLE_ZONE.get()));
		this.addContentBox(zoneRenameInput, FlexAlign.CENTER);

		zoneSize = new WidgetTextBox("", ChatFormatting.GRAY.getColor());
		zoneSize.setFont(ModFonts.TINY);
		zoneSize.addListener(MouseClickEvent.class, (event, widget) -> {
			if(selectedZone == null) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
			isEditingZoneSize = !isEditingZoneSize;
			zoneSizeEditor.setVisible(isEditingZoneSize);
			return WidgetEventResult.HANDLED;
		});
		this.addContentBox(zoneSize, FlexAlign.CENTER);

		zoneSizeEditor = new ZoneSizeEditor(selectedZone);
		zoneSizeEditor.setVisible(false);
		this.addContentBox(zoneSizeEditor, FlexAlign.CENTER);

		zoneDeviceLabel = new WidgetTextBox(DEVICES_LABEL.get(), ChatFormatting.WHITE.getColor());
		zoneDeviceLabel.setFont(ModFonts.NOKIA);
		this.addContentBox(zoneDeviceLabel, FlexAlign.START);

		devicesList = new WidgetVBox();
		devicesList.setSpacing(2);
		this.addContentBox(devicesList, FlexAlign.FILL);

		deleteIcon = new TrashButton(DELETE_ZONE);
		deleteIcon.setPosition(this.width() - 10, this.height() - 10);
		deleteIcon.addListener(MouseClickEvent.class, (event, widget) -> {
			if(selectedZone == null) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}
			if(!getGUI().isCtrlDown() || !getGUI().isShiftDown()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			var homeId = selectedZone.home().id();
			var zoneId = selectedZone.id();
			setSelectedZone(null);
			PacketDistributor.sendToServer(new MarkZoneAsDeletedPayload(homeId, zoneId, false));
			return WidgetEventResult.HANDLED;
		});
		this.add(deleteIcon);
	}

	public ZoneDetailWidget setSelectedZone(HomeZone selectedZone) {
		this.selectedZone = selectedZone;
		if(selectedZone == null) {
			zoneSize.setText("");
			zoneRenameInput.setValue("");
			zoneSizeEditor.setZone(null);
		} else {
			zoneRenameInput.setValue(selectedZone.name());
			zoneRenameInput.nativeWidget().moveCursorToStart(false);
			var bounds = selectedZone.bounds();
			int sizeX = (int)Math.round(bounds.maxX - bounds.minX);
			int sizeY = (int)Math.round(bounds.maxY - bounds.minY);
			int sizeZ = (int)Math.round(bounds.maxZ - bounds.minZ);
			zoneSize.setText(String.format("%dx%dx%d", sizeX, sizeY, sizeZ));
			zoneSizeEditor.setZone(selectedZone);

			devicesList.clear();
			for(var device : selectedZone.foundDevices()) {
				var deviceWidget = new WidgetTextBox(device.pos().toShortString() + " - " + I18n.get(device.state().getBlock().getDescriptionId()), 0xFFFFFF);
				deviceWidget.setFont(ModFonts.TINY);
				deviceWidget.autoWidth();
				deviceWidget.autoHeight();
				devicesList.addContentBox(deviceWidget, FlexAlign.FILL);
			}

			for(var device : selectedZone.devices().values()) {
				if(device.ignored()) {
					continue;
				}
				var deviceWidget = new WidgetTextBox(device.name(), ChatFormatting.GRAY.getColor());
				deviceWidget.autoWidth();
				deviceWidget.autoHeight();
				devicesList.addContentBox(deviceWidget, FlexAlign.FILL);
			}
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
		zoneDeviceLabel.autoWidth();
		zoneDeviceLabel.autoHeight();
		devicesList.setWidth(this.width() - 60);
		devicesList.setHeight(this.height() - (zoneDeviceLabel.x() + zoneDeviceLabel.height() + 4 + 2*paddingVertical + 40));
		deleteIcon.setPosition(this.width() - 18, this.height() - 18);
		zoneSizeEditor.setWidth(this.width() - 20);
		zoneSizeEditor.updateWidgetSizes();
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
