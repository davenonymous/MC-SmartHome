package com.davenonymous.smarthome.gui.home.main.devices.sensor;

import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.content.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseScrollEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTable;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.devices.SetSensorStatePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SensorTable extends WidgetTable {

	@I18DataGen(lang = "en_us", string = "Sensor")
	@I18DataGen(lang = "de_de", string = "Sensor")
	public static final I18String SENSOR_NAME_LABEL = I18String.gui("devices.detail", "sensor_name");

	@I18DataGen(lang = "en_us", string = "Active")
	@I18DataGen(lang = "de_de", string = "Aktiv")
	public static final I18String SENSOR_ACTIVE_LABEL = I18String.gui("devices.detail", "sensor_active");

	@I18DataGen(lang = "en_us", string = "Last Update")
	@I18DataGen(lang = "de_de", string = "Letzte Aktualisierung")
	public static final I18String SENSOR_LAST_UPDATE_LABEL = I18String.gui("devices.detail", "sensor_last_update");

	@I18DataGen(lang = "en_us", string = "Value")
	@I18DataGen(lang = "de_de", string = "Wert")
	public static final I18String SENSOR_VALUE_LABEL = I18String.gui("devices.detail", "sensor_value");


	public SensorTable() {
		super();

		this.setCellPaddingHorizontal(20);
		this.setCellPaddingVertical(5);
		this.alwaysShowFirstColumn = false;
		this.alwaysShowFirstRow = false;

		this.removeEventListeners(MouseScrollEvent.class);

	}

	public void populate(HomeZone zone, ConfiguredDevice device) {
		this.clear();

		var headerZoneLabel = new WidgetTextBox(SENSOR_NAME_LABEL.get());
		headerZoneLabel.setTextColor(0xFFFFFFFF);
		headerZoneLabel.setFont(ModFonts.NOKIA);
		headerZoneLabel.autoWidth(150);
		headerZoneLabel.autoHeight();
		this.add(0, 0, new CellData(headerZoneLabel, ContentAlignment.MIDDLE_LEFT));

		var headerDeviceLabel = new WidgetTextBox(SENSOR_ACTIVE_LABEL.get());
		headerDeviceLabel.setTextColor(0xFFFFFFFF);
		headerDeviceLabel.setFont(ModFonts.NOKIA);
		headerDeviceLabel.autoWidth(100);
		headerDeviceLabel.autoHeight();
		this.add(1, 0, new CellData(headerDeviceLabel, ContentAlignment.MIDDLE_LEFT));

		var headerBlockStateLabel = new WidgetTextBox(SENSOR_LAST_UPDATE_LABEL.get());
		headerBlockStateLabel.setTextColor(0xFFFFFFFF);
		headerBlockStateLabel.setFont(ModFonts.NOKIA);
		headerBlockStateLabel.autoWidth(150);
		headerBlockStateLabel.autoHeight();
		this.add(2, 0, new CellData(headerBlockStateLabel, ContentAlignment.MIDDLE_LEFT));

		var headerSpecificLabel = new WidgetTextBox(SENSOR_VALUE_LABEL.get());
		headerSpecificLabel.setTextColor(0xFFFFFFFF);
		headerSpecificLabel.setFont(ModFonts.NOKIA);
		headerSpecificLabel.autoWidth(150);
		headerSpecificLabel.autoHeight();
		this.add(3, 0, new CellData(headerSpecificLabel, ContentAlignment.MIDDLE_LEFT));

		List<? extends HomeSensor<?,?>> sensors = device.sensors().keySet().stream()
			.map(ModSensors::getById)
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(sensor -> sensor.getDisplayName().get()))
			.toList();

		int row = 1;
		for(var sensor : sensors) {
			var settings = device.sensors().get(sensor.id());
			var sensorName = new WidgetTextBox(sensor.getDisplayName().get());
			sensorName.setTextColor(0xFFAAAAAA);
			sensorName.autoWidth(150);
			sensorName.autoHeight();
			this.add(0, row, new CellData(sensorName, ContentAlignment.MIDDLE_LEFT));

			var sensorState = new WidgetToggle(settings.enabled());
			sensorState.addListener(
				ValueChangedEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(new SetSensorStatePayload(zone.home().id(), zone.id(), device.id(), sensor.id(), sensorState.getValue()));
					return WidgetEventResult.CONTINUE_PROCESSING;
				});
			this.add(1, row, new CellData(sensorState, ContentAlignment.MIDDLE_CENTER));

			var lastUpdate = new WidgetTextBox("?");
			lastUpdate.setTextColor(0xFFAAAAAA);
			lastUpdate.autoWidth(150);
			lastUpdate.autoHeight();
			this.add(2, row, new CellData(lastUpdate, ContentAlignment.MIDDLE_LEFT));

			var sensorValue = new WidgetTextBox("?");
			sensorValue.setTextColor(0xFFAAAAAA);
			sensorValue.autoWidth(150);
			sensorValue.autoHeight();
			this.add(3, row, new CellData(sensorValue, ContentAlignment.MIDDLE_LEFT));

			row++;
		}

		this.updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		this.setHeight(400);
		int yPos = 0;
		for(int row = 0; row < this.getRowCount(); row++) {
			int labelMaxWidths = this.width() - this.getColumnWidth(1) - 2 * this.paddingHorizontal() - 40;
			int rowHeight = 0;
			if(this.get(0, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
				rowHeight = Math.max(rowHeight, desc.getHeight());
			}
			if(this.get(2, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
				rowHeight = Math.max(rowHeight, desc.getHeight());
			}
			if(this.get(3, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
				rowHeight = Math.max(rowHeight, desc.getHeight());
			}
			yPos += rowHeight + this.paddingVertical() + 2;
		}

		this.setHeight(yPos + 16);
	}
}
