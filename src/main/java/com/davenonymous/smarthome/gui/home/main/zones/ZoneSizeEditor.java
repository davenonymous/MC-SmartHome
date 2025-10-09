package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTable;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.zones.SetZoneAABBPayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ZoneSizeEditor extends WidgetPanel {
	private HomeZone zone;

	private WidgetTable sizeTable;

	@I18DataGen(lang = "en_us", string = "Scale")
	@I18DataGen(lang = "de_de", string = "Skalieren")
	public static final I18String labelScale = I18String.gui("home.zone", "scale");

	@I18DataGen(lang = "en_us", string = "Move")
	@I18DataGen(lang = "de_de", string = "Verschieben")
	public static final I18String labelMove = I18String.gui("home.zone", "move");

	int padding = 6;
	public ZoneSizeEditor(HomeZone zone) {
		super();
		this.setHeight(60);
		this.setWidth(100);
		this.zone = zone;


		sizeTable = new WidgetTable();
		sizeTable.setCellPaddingHorizontal(3);
		sizeTable.setCellPaddingVertical(2);
		sizeTable.setDimensions(padding-2, padding, this.width()-2*padding+4, this.height()-2*padding);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			fillSizeTable();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		fillSizeTable();
		this.add(sizeTable);
	}

	private void fillSizeTable() {
		sizeTable.clear();
		if(zone == null) {
			return;
		}

		this.createHeaderRow();
		this.createScaleRow();
		this.createMoveRow();
		this.updateWidgetSizes();
	}

	private CellData createHeaderWidget(Direction direction) {
		return createHeaderWidget(direction, ContentAlignment.MIDDLE_CENTER);
	}

	private CellData createHeaderWidget(Direction direction, ContentAlignment alignment) {
		// TODO: LOCALIZE Directions once again
		String ucFirstDirection = direction.getName().substring(0, 1).toUpperCase();
		WidgetTextBox header = new WidgetTextBox(ucFirstDirection);
		header.setFont(ModFonts.NOKIA);
		header.autoWidth();
		header.autoHeight();
		header.setSize(header.width()-2, header.height()-2);
		header.setTextColor(0xFFFFFFFF);
		return new CellData(header, alignment);
	}

	private CellData createLabelWidget(String text) {
		WidgetTextBox header = new WidgetTextBox(text);
		header.setFont(ModFonts.NOKIA);
		header.autoWidth();
		header.autoHeight();
		header.setTextColor(0xFFFFFFFF);
		return new CellData(header, ContentAlignment.MIDDLE_LEFT);
	}

	private static final List<Direction> columnOrder = List.of(
		Direction.UP,
		Direction.DOWN,
		Direction.NORTH,
		Direction.EAST,
		Direction.SOUTH,
		Direction.WEST
	);

	private ZoneSizeEditor createHeaderRow() {
		sizeTable.add(0, 0, new Spacer(1, 1));

		int col = 1;
		for(var dir : columnOrder) {
			sizeTable.add(col++, 0, createHeaderWidget(dir));
		}
		return this;
	}

	private ZoneSizeEditor createScaleRow() {
		sizeTable.add(0, 1, createLabelWidget(labelScale.get()));
		int col = 1;
		for(var dir : columnOrder) {
			var box = new WidgetHBox();
			box.setSpacing(0);
			box.setSize(24, 12);
			var plusIcon = new WidgetSprite(HackerNoon.Regular.arrowCircleUp).setScale(0.5f).setColor(ChatFormatting.GRAY.getColor() | 0x90000000, ChatFormatting.DARK_GREEN.getColor() | 0xBB000000);
			var minusIcon = new WidgetSprite(HackerNoon.Regular.arrowCircleDown).setScale(0.5f).setColor(ChatFormatting.GRAY.getColor() | 0x90000000, ChatFormatting.DARK_GREEN.getColor() | 0xBB000000);
			if(!zone.canGrow(dir)) {
				plusIcon.setColor(ChatFormatting.RED.getColor() | 0x40000000);
			} else {
				plusIcon.addListener(MouseClickEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(SetZoneAABBPayload.grow(zone, dir));
					return WidgetEventResult.HANDLED;
				});
			}
			if(!zone.canShrink(dir)) {
				minusIcon.setColor(ChatFormatting.RED.getColor() | 0x40000000);
			} else {
				minusIcon.addListener(MouseClickEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(SetZoneAABBPayload.shrink(zone, dir));
					return WidgetEventResult.HANDLED;
				});
			}
			box.addContentBox(plusIcon);
			box.addContentBox(minusIcon);
			sizeTable.add(col++, 1, box);
		}

		return this;
	}

	private ZoneSizeEditor createMoveRow() {
		sizeTable.add(0, 2, createLabelWidget(labelMove.get()));
		int col = 1;
		for(var dir : columnOrder) {
			var plusIcon = new WidgetSprite(HackerNoon.Regular.plus).setScale(0.5f)
				.setColor(ChatFormatting.GRAY.getColor() | 0x90000000, ChatFormatting.DARK_GREEN.getColor() | 0xBB000000);
			if(!zone.canGrow(dir)) {
				plusIcon.setColor(ChatFormatting.RED.getColor() | 0x40000000);
			} else {
				plusIcon.addListener(MouseClickEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(SetZoneAABBPayload.move(zone, dir));
					return WidgetEventResult.HANDLED;
				});
			}
			sizeTable.add(col++, 2, plusIcon);
		}

		return this;
	}

	public ZoneSizeEditor setZone(HomeZone zone) {
		this.zone = zone;
		fillSizeTable();
		return this;
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		sizeTable.setDimensions(padding, padding, this.width()-2*padding, this.height()-2*padding);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
