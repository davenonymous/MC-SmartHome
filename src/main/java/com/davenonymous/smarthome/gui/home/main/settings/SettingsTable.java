package com.davenonymous.smarthome.gui.home.main.settings;

import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTable;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

public class SettingsTable extends WidgetTable {

	public SettingsTable() {
		super();
		this.setCellPaddingHorizontal(20);
		this.setCellPaddingVertical(5);
		this.alwaysShowFirstColumn = false;
		this.alwaysShowFirstRow = false;
	}


	public <W extends Widget> SettingsTable addSetting(String label, W setting, String description, Function<W, TooltipComponent> tooltipProvider) {
		setting.addListener(ValueChangedEvent.class, ((event, widget) -> {
			var tooltip = tooltipProvider.apply(setting);
			setting.setTooltipElements(tooltip);
			this.fireEvent(new ValueChangedEvent<>(event.oldValue, event.newValue));
			return WidgetEventResult.CONTINUE_PROCESSING;
		}));
		var tooltip = tooltipProvider.apply(setting);
		setting.setTooltipElements(tooltip);

		var row = this.getRowCount();
		var labelWidget = new WidgetTextBox(label);
		labelWidget.setWordWrap(true);
		labelWidget.setFont(ModFonts.NOKIA);
		labelWidget.setTextColor(0xFFFFFFFF);
		labelWidget.autoWidth(150);
		labelWidget.autoHeight();
		this.add(0, row, new CellData(labelWidget, ContentAlignment.TOP_LEFT));

		this.add(1, row, new CellData(setting, ContentAlignment.TOP_LEFT));

		var descWidget = new WidgetTextBox(description);
		descWidget.setTextColor(0xFFAAAAAA);
		descWidget.setWordWrap(true);
		descWidget.autoWidth(200);
		descWidget.autoHeight();
		this.add(2, row, new CellData(descWidget, ContentAlignment.TOP_LEFT));

		return this;
	}


	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		for(int row = 0; row < this.getRowCount(); row++) {
			int labelMaxWidths = this.width() - this.getColumnWidth(1) - 2 * this.paddingHorizontal() - 40;
			if(this.get(0, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
			}
			if(this.get(2, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
			}
		}
	}
}
