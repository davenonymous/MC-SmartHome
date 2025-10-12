package com.davenonymous.smarthome.gui.home.main.cards.vizsettings;

import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanelWithChoiceValue;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.content.visualization.VizLegendStyle;

public class LegendStyleSelector extends WidgetPanelWithChoiceValue<VizLegendStyle> {
	WidgetSprite icon;

	public LegendStyleSelector() {
		super();
		this.setHeight(12);
		this.setWidth(100);

		var legendLabel = new WidgetTextBox("Legend:");
		legendLabel.setPosition(0, 1);
		legendLabel.setFont(ModFonts.NOKIA);
		legendLabel.autoWidth();
		this.add(legendLabel);

		icon = new WidgetSprite(VizLegendStyle.BOTTOM.icon(), 0xFFAAAAAA, ColorHelper.COLOR_ORANGE);
		icon.setPosition(legendLabel.width + 4, 0);
		this.add(icon);

		this.addClickListener();

		this.addListener(ValueChangedEvent.class, (event, widget) -> {
			icon.setSprite(getValue().icon());
			icon.autoSize();
			icon.setScale(0.5f);
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		this.addChoice(VizLegendStyle.values());
		this.setValue(VizLegendStyle.BOTTOM);

		this.setHeight(12);
		this.setWidth(legendLabel.width + 4 + icon.width);
	}
}
