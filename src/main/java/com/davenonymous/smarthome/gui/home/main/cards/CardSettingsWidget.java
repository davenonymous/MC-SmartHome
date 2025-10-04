package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class CardSettingsWidget extends WidgetVBox {
	HomeCard card = null;

	@I18DataGen(lang = "en_us", string = "Card Settings")
	@I18DataGen(lang = "de_de", string = "Karten Einstellungen")
	public static final I18String CARD_SETTINGS_TITLE = SmartHome.guiString("home.cards.card_settings", "title");

	public CardSettingsWidget() {
		super();
		this.setPadding(6);
		this.setWidth(160);
		this.setHeight(40);

		setCard(null);
	}

	public CardSettingsWidget setCard(HomeCard card) {
		this.card = card;
		this.clear();

		var title = new WidgetTextBox(CARD_SETTINGS_TITLE.get(), 0xFFFFFFFF);
		title.setWordWrap(true);
		title.setFont(ModFonts.SAMSUNG);
		title.autoWidth(this.width - 16);
		title.autoHeight();
		this.addContentBox(title, FlexAlign.CENTER);

		this.updateWidgetSizes();
		this.adjustSizeToContent(false);
		this.setWidth(Math.max(this.width, 160));
		return this;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
