package com.davenonymous.smarthome.gui.home.main.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.gui.events.ElementSettingsChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class ElementSettingsWidget extends WidgetVBox {

	@I18DataGen(lang = "en_us", string = "No element selected")
	@I18DataGen(lang = "de_de", string = "Kein Element ausgewählt")
	public static final I18String NO_ELEMENT_SELECTED = SmartHome.guiString("home.cards.element_settings", "no_element_selected");

	@I18DataGen(lang = "en_us", string = "Select an element to edit its settings")
	@I18DataGen(lang = "de_de", string = "Wähle ein Element, um dessen Einstellungen zu bearbeiten")
	public static final I18String SELECT_ELEMENT_HINT = SmartHome.guiString("home.cards.element_settings", "select_element_hint");

	@I18DataGen(lang = "en_us", string = "No settings available for this element")
	@I18DataGen(lang = "de_de", string = "Keine Einstellungen für dieses Element verfügbar")
	public static final I18String NO_SETTINGS_AVAILABLE = SmartHome.guiString("home.cards.element_settings", "no_settings_available");

	@I18DataGen(lang = "en_us", string = "Element Settings")
	@I18DataGen(lang = "de_de", string = "Element Einstellungen")
	public static final I18String ELEMENT_SETTINGS_TITLE = SmartHome.guiString("home.cards.element_settings", "title");

	HomeCardElement<?> element = null;

	public ElementSettingsWidget() {
		super();
		this.setPadding(6);
		this.setWidth(160);
		this.setHeight(40);

		setElement(null);
	}

	public ElementSettingsWidget setElement(HomeCardElement<?> element) {
		this.element = element;
		this.clear();

		var title = new WidgetTextBox(ELEMENT_SETTINGS_TITLE.get(), 0xFFFFFFFF);
		title.setWordWrap(true);
		title.setFont(ModFonts.SAMSUNG);
		title.autoWidth(this.width - 16);
		title.autoHeight();
		this.addContentBox(title, FlexAlign.CENTER);

		if(element != null) {
			var settingsWidgets = element.createSettingWidgets();
			if(settingsWidgets.isEmpty()) {
				var label = new WidgetTextBox(NO_SETTINGS_AVAILABLE.get(), 0xFFAAAAAA);
				label.setWordWrap(true);
				label.autoWidth(this.width - 16);
				label.autoHeight();
				this.addContentBox(label, FlexAlign.CENTER);
			} else {
				for(var w : settingsWidgets) {
					w.addListener(ValueChangedEvent.class, (event, widget) -> {
						this.fireEvent(new ElementSettingsChangedEvent(element.id(), element.loadSettings(settingsWidgets)));
						return WidgetEventResult.CONTINUE_PROCESSING;
					});
					this.addContentBox(w, FlexAlign.START);
				}
			}
		} else {
			var label = new WidgetTextBox(NO_ELEMENT_SELECTED.get(), 0xFFAAAAAA);
			label.setWordWrap(true);
			label.autoWidth(this.width - 16);
			label.autoHeight();
			label.setTooltipElements(
				WrappedStringTooltipComponent.orange(SELECT_ELEMENT_HINT.get())
			);
			this.addContentBox(label, FlexAlign.CENTER);
		}

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
