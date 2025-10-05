package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.event.MouseEnterEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseExitEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;

public class AddCardElementSelectionWidget extends WidgetHBox {
	WidgetTextBox label;
	WidgetSprite icon;

	int normalColor = ChatFormatting.WHITE.getColor() | 0xFF000000;
	int activeColor = ColorHelper.COLOR_GREEN;
	int hoverColor = ColorHelper.COLOR_ORANGE;

	public AddCardElementSelectionWidget(ResourceLocation elementId) {
		super();
		this.setWidth(100);
		this.setHeight(32);
		this.setSpacing(6);

		icon = new WidgetSprite(ModCardElements.ICON_BY_ID.get(elementId));
		icon.setColor(normalColor);
		icon.setScale(0.5f);
		this.addContentBox(icon, FlexAlign.CENTER);

		label = new WidgetTextBox(ModCardElements.NAME_BY_ID.get(elementId).get(), normalColor);
		//label.setFont(ModFonts.NOKIA);
		label.autoWidth();
		label.autoHeight();
		this.addContentBox(label, FlexAlign.END);

		this.adjustSizeToContent();

		this.addListener(
			MouseEnterEvent.class, (event, widget) -> {
				icon.setColor(hoverColor);
				label.setTextColor(hoverColor);
				return WidgetEventResult.CONTINUE_PROCESSING;
			});

		this.addListener(
			MouseExitEvent.class, (event, widget) -> {
				icon.setColor(normalColor);
				label.setTextColor(normalColor);
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
	}

}
