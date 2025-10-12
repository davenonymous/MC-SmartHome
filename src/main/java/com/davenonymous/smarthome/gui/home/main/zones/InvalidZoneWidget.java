package com.davenonymous.smarthome.gui.home.main.zones;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.items.RangerFinderDataComponent;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class InvalidZoneWidget extends WidgetPanel {
	public WidgetTextBox intersectsText;
	public WidgetSprite warningIcon;
	public WidgetTextBox dimensionsText;

	@I18DataGen(lang = "en_us", string = "The Range Finder area intersects with an existing zone")
	@I18DataGen(lang = "de_de", string = "Der Bereich des Entfernungsmessers überschneidet sich mit einer bestehenden Zone")
	public static final I18String RANGE_FINDER_INTERSECTS = I18String.gui("home.zones", "range_finder.intersects");

	@I18DataGen(lang = "en_us", string = "Invalid")
	@I18DataGen(lang = "de_de", string = "Ungültig")
	public static final I18String INVALID_ZONE = I18String.gui("home.zones", "label.invalid_zone");

	public InvalidZoneWidget(ZonesContainer parent, RangerFinderDataComponent rangeFinderData) {
		this.setWidth(100);
		this.setHeight(32);

		intersectsText = new WidgetTextBox(INVALID_ZONE.get());
		intersectsText.setWordWrap(false);
		intersectsText.setTextColor(0xFF000000);
		this.add(intersectsText);

		warningIcon = new WidgetSprite(HackerNoon.Solid.exclamation);
		warningIcon.setColor(0xFFAAAAAA);
		warningIcon.scale = 0.5f;
		warningIcon.setColor(0xFF904444);
		this.add(warningIcon);

		dimensionsText = new WidgetTextBox(rangeFinderData.sizeText());
		dimensionsText.autoWidth();
		dimensionsText.setFont(ModFonts.TINY);
		dimensionsText.setTextColor(0xFFFFFFFF);
		this.add(dimensionsText);

		updateWidgetSizes();
		this.addTooltipElement(WrappedStringTooltipComponent.red(RANGE_FINDER_INTERSECTS.get()));
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		intersectsText.autoWidth();
		intersectsText.setHeight(8);
		dimensionsText.setPosition(2, 26);

		warningIcon.setPosition(4, (24 - (int)(0.5f * warningIcon.height())) / 2);
		intersectsText.setPosition(warningIcon.x + (int)(warningIcon.width() / 2f) + 2, (24 - intersectsText.height()) / 2);

		this.setWidth(intersectsText.x + intersectsText.width + 4);
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.BUTTON_R3), 0, 0, this.width, 24);
		super.draw(guiGraphics, window);
	}
}
