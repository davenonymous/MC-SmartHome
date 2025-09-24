package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.items.RangerFinderDataComponent;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.networking.actions.AddNewZonePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class AddZoneButtonWidget extends WidgetPanel {
	public StringInputWidget newZoneNameInput;
	public WidgetSprite plusIcon;
	public WidgetTextBox dimensionsText;

	public AddZoneButtonWidget(ZonesContainer parent, RangerFinderDataComponent rangeFinderData) {
		this.setWidth(100);
		this.setHeight(32);

		newZoneNameInput = new StringInputWidget("", "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		newZoneNameInput.setDrawBackground(false);
		newZoneNameInput.nativeWidget().setHint(Component.literal(I18n.get("smarthome.gui.home.zones.add_zones.hint")));
		newZoneNameInput.nativeWidget().setBordered(false);
		newZoneNameInput.nativeWidget().setTextColor(ChatFormatting.DARK_GRAY.getColor());
		newZoneNameInput.addListener(
			ValueChangedEvent.class, (event, widget) -> {

				return WidgetEventResult.HANDLED;
			});

		this.add(newZoneNameInput);


		plusIcon = new WidgetSprite(HackerNoon.Solid.plus);
		plusIcon.setColor(0xFFAAAAAA);
		plusIcon.scale = 0.5f;
		plusIcon.addListener(MouseEnterEvent.class, (event, widget) -> {
			if(newZoneNameInput.getValue().isEmpty()) {
				plusIcon.setColor(0xFF904444);
			} else {
				plusIcon.setColor(0xFF449044);
			}
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		plusIcon.addListener(MouseExitEvent.class, (event, widget) -> {
			plusIcon.setColor(0xFFAAAAAA);
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
		plusIcon.addListener(MouseClickEvent.class, (event, widget) -> {
			if(newZoneNameInput.getValue().isEmpty()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			PacketDistributor.sendToServer(new AddNewZonePayload(HomeScreen.get().selectedHome.id(), rangeFinderData.toAABB(), newZoneNameInput.getValue()));
			return WidgetEventResult.HANDLED;
		});

		this.add(plusIcon);

		dimensionsText = new WidgetTextBox(rangeFinderData.sizeText());
		dimensionsText.autoWidth();
		dimensionsText.setFont(ModFonts.TINY);
		dimensionsText.setTextColor(0xFFFFFFFF);
		this.add(dimensionsText);

		updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		newZoneNameInput.setWidth(75);
		newZoneNameInput.setHeight(15);
		newZoneNameInput.setPosition(4, (24 - newZoneNameInput.height()) / 2);

		plusIcon.setPosition(newZoneNameInput.width + 4, (24 - (int)(0.5f * plusIcon.height())) / 2);
		dimensionsText.setPosition(2, 26);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R3), 0, 0, this.width, 24);
		super.draw(guiGraphics, window);
	}
}
