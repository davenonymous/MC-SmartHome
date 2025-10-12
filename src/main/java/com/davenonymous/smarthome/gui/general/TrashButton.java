package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.i18n.I18String;

public class TrashButton extends WidgetSprite {
	public TrashButton(I18String tooltip) {
		super(HackerNoon.Regular.trashAlt, 0xFFAAAAAA, ColorHelper.COLOR_ERRORED.getRGB() | 0xFF000000);
		this.setScale(0.5f);
		this.setTooltipElements(WrappedStringTooltipComponent.orange(tooltip.get()));
	}
}
