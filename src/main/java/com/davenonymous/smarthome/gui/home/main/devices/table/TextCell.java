package com.davenonymous.smarthome.gui.home.main.devices.table;

import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModFonts;

public class TextCell extends WidgetVBox {
	private WidgetTextBox labelWidget;
	private WidgetTextBox descriptionWidget;

	public TextCell(String label) {
		this(label, null);
	}

	public TextCell(String label, String description) {
		super();
		this.setPadding(0);
		this.setSpacing(1);
		this.setWidth(400);
		this.setHeight(200);

		labelWidget = new WidgetTextBox(label);
		labelWidget.autoWidth();
		labelWidget.autoHeight();
		labelWidget.setTextColor(0xFFAAAAAA);
		this.addContentBox(labelWidget, FlexAlign.START);

		descriptionWidget = new WidgetTextBox(description != null ? description : "");
		descriptionWidget.setFont(ModFonts.TINY);
		descriptionWidget.autoWidth();
		descriptionWidget.autoHeight();
		descriptionWidget.setTextColor(0xFFAAAAAA);
		this.addContentBox(descriptionWidget, FlexAlign.START);

		updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		labelWidget.autoWidth();
		labelWidget.autoHeight();
		descriptionWidget.autoWidth();
		descriptionWidget.autoHeight();

		if(description().isBlank()) {
			descriptionWidget.setVisible(false);
		} else {
			descriptionWidget.setVisible(true);
		}

		int thisWidth = 5 + Math.max(labelWidget.width(), descriptionWidget.width());
		this.setSize(
			thisWidth,
			labelWidget.height() + (descriptionWidget.isVisible() ? (1 + descriptionWidget.height()) : 0)
		);
	}

	public String description() {
		return descriptionWidget.getText();
	}

	public String label() {
		return labelWidget.getText();
	}

	public void setTextColor(int color) {
		labelWidget.setTextColor(color);
		descriptionWidget.setTextColor(color);
	}
}
