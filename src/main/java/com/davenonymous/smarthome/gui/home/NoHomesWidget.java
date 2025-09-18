package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class NoHomesWidget extends WidgetPanel {
	WidgetTextBox titleText;
	WidgetTextBox outlineText;
	WidgetTextBox hintText;

	public NoHomesWidget(HomeScreen homeScreen) {
		this.setWidth(150);
		this.setHeight(300);

		this.titleText = new WidgetTextBox(I18n.get("smarthome.gui.home.no_homes"));
		this.titleText.setTextColor(ChatFormatting.RED.getColor());
		this.titleText.setWordWrap(true);
		this.titleText.setFont(ModFonts.MONKEY_FILLED);
		this.add(this.titleText);

		this.outlineText = new WidgetTextBox(I18n.get("smarthome.gui.home.no_homes"));
		this.outlineText.setTextColor(ChatFormatting.WHITE.getColor());
		this.outlineText.setWordWrap(true);
		this.outlineText.setFont(ModFonts.MONKEY_OUTLINE);
		this.add(this.outlineText);

		this.hintText = new WidgetTextBox(I18n.get("smarthome.gui.home.no_homes.hint"));
		this.hintText.setTextColor(ChatFormatting.DARK_GREEN.getColor());
		this.hintText.setWordWrap(true);
		this.hintText.setFont(ModFonts.NOKIA);
		this.add(this.hintText);


		updateWidgetSizes();
	}

	public void updateWidgetSizes() {
		this.titleText.autoWidth(this.width);
		this.titleText.autoHeight();
		this.titleText.setX((this.width - this.titleText.width()) / 2);
		this.outlineText.autoWidth(this.width);
		this.outlineText.autoHeight();
		this.outlineText.setX((this.width - this.outlineText.width()) / 2);
		this.hintText.setY(this.titleText.height() + 10);
		this.hintText.autoWidth(this.width);
		this.hintText.autoHeight();
		this.hintText.setX((this.width - this.hintText.width()) / 2);
		int maxWidth = Math.max(this.titleText.width(), this.hintText.width());
		this.setWidth(maxWidth + 20);
		this.setHeight(this.titleText.height() + 20 + this.hintText.height());
	}
}
