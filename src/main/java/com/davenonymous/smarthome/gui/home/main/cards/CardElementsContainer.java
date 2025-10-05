package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class CardElementsContainer extends WidgetVBox {
	@I18DataGen(lang = "en_us", string = "Add element")
	@I18DataGen(lang = "de_de", string = "Element hinzufügen")
	public static final I18String ADD_CARD_ELEMENT_TITLE = SmartHome.guiString("home.cards.elements", "title");

	public CardElementsContainer() {
		super();
		this.setPadding(6);
		this.setWidth(160);
		this.setHeight(40);

		var title = new WidgetTextBox(ADD_CARD_ELEMENT_TITLE.get(), 0xFFFFFFFF);
		title.setWordWrap(true);
		title.setFont(ModFonts.SAMSUNG);
		title.autoWidth(this.width - 16);
		title.autoHeight();
		this.addContentBox(title, FlexAlign.CENTER);

		for(var elementType : ModCardElements.getAllSorted()) {
			ResourceLocation id = elementType.getFirst();
			var elementChoiceWidget = new AddCardElementSelectionWidget(id);
			this.addContentBox(elementChoiceWidget, FlexAlign.FILL);
		}

		this.adjustSizeToContent(false);
		this.setWidth(Math.max(this.width, 160));

	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.fill(0, 0, width(), height(), 0x88AAAAAA);
		guiGraphics.fill(1, 1, width()-1, height()-1, 0xFF222222);

		super.draw(guiGraphics, window);
	}
}
