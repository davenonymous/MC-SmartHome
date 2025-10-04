package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.event.MouseEnterEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseExitEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

public class CardSelectionWidget extends WidgetHBox {
	WidgetTextBox label;
	WidgetSprite icon;

	boolean isActive = false;
	int normalColor = ChatFormatting.WHITE.getColor() | 0xFF000000;
	int activeColor = ColorHelper.COLOR_GREEN;
	int hoverColor = ColorHelper.COLOR_ORANGE;

	public CardSelectionWidget(HomeCard card) {
		super();
		this.setWidth(100);
		this.setHeight(32);
		this.setSpacing(6);

		icon = new WidgetSprite(card.icon());
		icon.setColor(normalColor);
		icon.setScale(0.5f);
		this.addContentBox(icon, FlexAlign.CENTER);

		label = new WidgetTextBox(card.label(), normalColor);
		label.setFont(ModFonts.SAMSUNG);
		label.autoWidth();
		label.autoHeight();
		this.addContentBox(label, FlexAlign.CENTER);

		this.addListener(
			MouseEnterEvent.class, (event, widget) -> {
				icon.setColor(hoverColor);
				label.setTextColor(hoverColor);
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
		this.addListener(
			MouseExitEvent.class, (event, widget) -> {
				icon.setColor(isActive ? activeColor : normalColor);
				label.setTextColor(isActive ? activeColor : normalColor);
				return WidgetEventResult.CONTINUE_PROCESSING;
			});

		this.adjustSizeToContent();
	}

	public CardSelectionWidget setActive(boolean active) {
		isActive = active;
		if(active) {
			icon.setColor(activeColor);
			label.setTextColor(activeColor);
		} else {
			icon.setColor(normalColor);
			label.setTextColor(normalColor);
		}

		return this;
	}
}
