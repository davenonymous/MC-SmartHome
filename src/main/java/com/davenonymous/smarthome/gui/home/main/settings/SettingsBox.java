package com.davenonymous.smarthome.gui.home.main.settings;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;

public class SettingsBox extends WidgetPanel {
	WidgetTextBox label;
	SettingsTable settingsTable;

	public SettingsBox(String labelText) {
		super();

		label = new WidgetTextBox(labelText);
		label.setPosition(5, 5);
		label.setFont(ModFonts.BASEL);
		label.autoWidth();
		label.autoHeight();
		label.setTextColor(0xFFFFFFFF);
		this.add(label);

		settingsTable = new SettingsTable();
		settingsTable.setPosition(8, label.y() + label.height() + 13);
		this.add(this.settingsTable);
	}

	public SettingsTable getSettingsTable() {
		return settingsTable;
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		int yOffset = label.height() + 10;

		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, yOffset, this.width, this.height - yOffset);
		guiGraphics.fill(3, yOffset+3, width()-3, height()-3, 0x88000000);

		super.draw(guiGraphics, window);
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		settingsTable.setSize(this.width - 16, this.height - (label.height() + 18));
		settingsTable.updateWidgetSizes();
		this.adjustSizeToContent(false);
	}
}
