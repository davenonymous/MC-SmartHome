package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

public class NoHomesWidget extends WidgetPanel {
	WidgetTextBox titleText;
	WidgetTextBox outlineText;
	WidgetTextBox hintText;


	@I18DataGen(lang = "en_us", string = "No Smart Home Found")
	@I18DataGen(lang = "de_de", string = "Kein Smart Home gefunden")
	public static final I18String NO_HOMES = I18String.gui("home", "no_homes");

	@I18DataGen(lang = "en_us", string = "Build a mini rack and place a Smart Home server in it to get started.")
	@I18DataGen(lang = "de_de", string = "Baue einen Mini-Rack und platzieren einen Smart Home-Server darin, um loszulegen.")
	public static final I18String NO_HOMES_HINT = I18String.gui("home", "no_homes.hint");

	public NoHomesWidget() {
		this.setWidth(150);
		this.setHeight(300);

		this.titleText = new WidgetTextBox(NO_HOMES.get());
		this.titleText.setTextColor(ChatFormatting.RED.getColor());
		this.titleText.setWordWrap(true);
		this.titleText.setFont(ModFonts.MONKEY_FILLED);
		this.add(this.titleText);

		this.outlineText = new WidgetTextBox(NO_HOMES.get());
		this.outlineText.setTextColor(ChatFormatting.WHITE.getColor());
		this.outlineText.setWordWrap(true);
		this.outlineText.setFont(ModFonts.MONKEY_OUTLINE);
		this.add(this.outlineText);

		this.hintText = new WidgetTextBox(NO_HOMES_HINT.get());
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
