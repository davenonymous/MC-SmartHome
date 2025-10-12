package com.davenonymous.smarthome.content.visualization.impl.line;

import com.davenonymous.smarthome.content.sensor.SensorColumn;
import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.MouseEnterEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseExitEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetColorSelect;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTable;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.setup.content.ModFonts;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;

public class SeriesSettingsWidget extends WidgetVBox {
	ConfiguredDevice device;
	HomeSensor<?,?> sensor;
	WidgetTable seriesTable;

	Map<Integer, SensorColumn> columnByRow = new HashMap<>();
	public SeriesSettingsWidget(ConfiguredDevice device, HomeSensor<?,?> sensor, Map<String, LineVizColumnSettings> series) {
		super();
		this.setSize(150, 300);
		this.device = device;
		this.sensor = sensor;

		var deviceLabel = new WidgetTextBox(device.name());
		deviceLabel.setFont(ModFonts.NOKIA);
		deviceLabel.autoWidth();
		deviceLabel.autoHeight();
		this.addContentBox(deviceLabel);

		seriesTable = new WidgetTable();
		seriesTable.setCellPaddingHorizontal(4);
		seriesTable.setCellPaddingVertical(2);
		seriesTable.setHeight(300);
		seriesTable.setWidth(150);

		int totalHeight = 0;
		int tableRow = 0;
		for(var column : sensor.getColumns()) {
			if(!column.type().isNumeric()) {
				continue;
			}


			String columnDisplayName = column.label().get(); //I18n.get(column.translationKey());
			LineVizColumnSettings seriesSettings;
			if(series == null || series.get(column.name()) == null) {
				seriesSettings = new LineVizColumnSettings(false, ColorHelper.COLOR_CYAN, columnDisplayName);
			} else {
				seriesSettings = series.get(column.name());
			}

			//var seriesLabel = createColumnLabel(seriesSettings.label());
			var seriesLabel = new StringInputWidget(seriesSettings.label(), ModFonts.SAFE_FONT_CHARS);
			seriesLabel.setWidth(80);
			seriesLabel.setHeight(11);
			seriesLabel.setDrawBackground(false);
			seriesLabel.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
			seriesLabel.nativeWidget().setYTextOffset(3);
			seriesLabel.addListener(ValueChangedEvent.class, (event, widget) -> this.fireEvent(event));
			seriesLabel.addListener(MouseEnterEvent.class, (event, widget) -> {
				seriesLabel.nativeWidget().setTextColor(ColorHelper.COLOR_ORANGE);
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
			seriesLabel.addListener(MouseExitEvent.class, (event, widget) -> {
				seriesLabel.nativeWidget().setTextColor(ChatFormatting.WHITE.getColor());
				return WidgetEventResult.CONTINUE_PROCESSING;
			});

			var toggle = new WidgetToggle(seriesSettings.enabled());
			toggle.addListener(ValueChangedEvent.class, (event, widget) -> this.fireEvent(event));

			var colorSelect = new WidgetColorSelect(seriesSettings.color());
			colorSelect.addListener(ValueChangedEvent.class, (event, widget) -> this.fireEvent(event));

			columnByRow.put(tableRow, column);
			seriesTable.add(0, tableRow, new CellData(seriesLabel, ContentAlignment.MIDDLE_LEFT));
			seriesTable.add(1, tableRow, toggle);
			seriesTable.add(2, tableRow, new CellData(colorSelect, ContentAlignment.MIDDLE_CENTER));
			tableRow++;

			totalHeight += Math.max(seriesLabel.height(), toggle.height()) + seriesTable.paddingVertical();
		}

		seriesTable.setHeight(totalHeight + seriesTable.paddingVertical() + 8);

		this.addContentBox(seriesTable);
		this.setHeight(this.getTotalRealSize());
	}

	public ConfiguredDevice device() {
		return device;
	}

	public HomeSensor<?, ?> sensor() {
		return sensor;
	}

	public Map<String, LineVizColumnSettings> currentSettings() {
		Map<String, LineVizColumnSettings> result = new HashMap<>();
		for(int row = 0; row < seriesTable.getRowCount(); row++) {
			var labelCell = seriesTable.get(0, row);
			var toggleCell = seriesTable.get(1, row);
			var colorCell = seriesTable.get(2, row);
			if(labelCell == null || toggleCell == null || colorCell == null) {
				continue;
			}

			var column = columnByRow.get(row);
			var labelWidget = (StringInputWidget)labelCell.widget();
			var toggleWidget = (WidgetToggle)toggleCell.widget();
			var colorWidget = (WidgetColorSelect)colorCell.widget();

			String columnName = labelWidget.getValue();
			boolean enabled = toggleWidget.getValue();

			result.put(column.name(), new LineVizColumnSettings(enabled, colorWidget.getValue(), columnName));
		}
		return result;
	}

	private WidgetTextBox createColumnLabel(String text) {
		var wigget = new WidgetTextBox(text);
		wigget.autoWidth();
		wigget.autoHeight();
		return wigget;
	}
}
