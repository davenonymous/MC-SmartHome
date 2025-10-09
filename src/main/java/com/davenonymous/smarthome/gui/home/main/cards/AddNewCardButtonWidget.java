package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.*;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.cards.AddNewCardPayload;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class AddNewCardButtonWidget extends WidgetPanel {
	public StringInputWidget newCardNameInput;
	public WidgetSprite plusIcon;

	@I18DataGen(lang = "en_us", string = "Enter name")
	@I18DataGen(lang = "de_de", string = "Name eingeben")
	public static final I18String CLICK_TO_RENAME = I18String.gui("home.cards", "add_cards.hint");


	public AddNewCardButtonWidget() {
		this.setWidth(100);
		this.setHeight(32);

		newCardNameInput = new StringInputWidget("", "[a-zA-Z0-9_ -!?+:/\\@#$%^&*()]*");
		newCardNameInput.setDrawBackground(false);
		newCardNameInput.nativeWidget().setHint(Component.translatable(CLICK_TO_RENAME.key()));
		newCardNameInput.nativeWidget().setBordered(false);
		newCardNameInput.nativeWidget().setTextColor(ChatFormatting.DARK_GRAY.getColor());
		this.add(newCardNameInput);


		plusIcon = new WidgetSprite(HackerNoon.Solid.plus);
		plusIcon.setColor(0xFFAAAAAA);
		plusIcon.setScale(0.5f);
		plusIcon.addListener(MouseEnterEvent.class, (event, widget) -> {
			if(newCardNameInput.getValue().isEmpty()) {
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
			if(newCardNameInput.getValue().isEmpty()) {
				return WidgetEventResult.CONTINUE_PROCESSING;
			}

			PacketDistributor.sendToServer(new AddNewCardPayload(HomeScreen.get().selectedHome.id(), newCardNameInput.getValue()));
			newCardNameInput.setValue("");
			newCardNameInput.nativeWidget().setFocused(false);
			newCardNameInput.nativeWidget().setHint(Component.translatable(CLICK_TO_RENAME.key()));
			return WidgetEventResult.HANDLED;
		});

		this.add(plusIcon);

		updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		newCardNameInput.setWidth(75);
		newCardNameInput.setHeight(15);
		newCardNameInput.setPosition(4, (24 - newCardNameInput.height()) / 2);

		plusIcon.setPosition(newCardNameInput.width + 4, (27 - newCardNameInput.height()) / 2);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R3), 0, 0, this.width, 24);
		super.draw(guiGraphics, window);
	}
}
