package com.davenonymous.smarthome.gui.home.main.settings;

import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.CellData;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.ContentAlignment;
import com.davenonymous.smarthome.lib.gui.event.MouseClickEvent;
import com.davenonymous.smarthome.lib.gui.event.MouseScrollEvent;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetSprite;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTable;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.ClientCache;
import com.davenonymous.smarthome.networking.actions.devices.AddDevicePayload;
import com.davenonymous.smarthome.networking.actions.devices.SetDeviceIgnorePayload;
import com.davenonymous.smarthome.setup.content.ModFonts;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class IgnoredDevicesTable extends WidgetTable {

	@I18DataGen(lang = "en_us", string = "Zone")
	@I18DataGen(lang = "de_de", string = "Zone")
	public static final I18String HEADER_LABEL_ZONE = I18String.gui("settings.ignored_devices.header", "zone");

	@I18DataGen(lang = "en_us", string = "Registered device")
	@I18DataGen(lang = "de_de", string = "Registriertes Gerät")
	public static final I18String HEADER_LABEL_DEVICE = I18String.gui("settings.ignored_devices.header", "device");

	@I18DataGen(lang = "en_us", string = "Sensors")
	@I18DataGen(lang = "de_de", string = "Sensoren")
	public static final I18String HEADER_LABEL_SPECIFIC_SENSORS = I18String.gui("settings.ignored_devices.header", "specific_sensors");

	@I18DataGen(lang = "en_us", string = "Current blockstate")
	@I18DataGen(lang = "de_de", string = "Aktueller Blockzustand")
	public static final I18String HEADER_LABEL_BLOCK_STATE = I18String.gui("settings.ignored_devices.header", "block_state");

	@I18DataGen(lang = "en_us", string = "Restore")
	@I18DataGen(lang = "de_de", string = "Wiederherstellen")
	public static final I18String HEADER_LABEL_RESTORE = I18String.gui("settings.ignored_devices.header", "restore");

	@I18DataGen(lang = "en_us", string = "Show specific sensors only")
	@I18DataGen(lang = "de_de", string = "Nur spezifische Sensoren anzeigen")
	public static final I18String TOOLTIP_SHOW_SPECIFIC_ONLY = I18String.gui("settings.ignored_devices.header", "show_specific_only");

	private Map<HomeZone, List<ConfiguredDevice>> allConfiguredDevices;
	private boolean hideGenericDevices = true;

	public IgnoredDevicesTable(Map<HomeZone, List<ConfiguredDevice>> allConfiguredDevices) {
		super();
		this.setCellPaddingHorizontal(20);
		this.setCellPaddingVertical(5);
		this.alwaysShowFirstColumn = false;
		this.alwaysShowFirstRow = false;
		this.allConfiguredDevices = allConfiguredDevices;

		this.removeEventListeners(MouseScrollEvent.class);
		this.populate();
	}

	public void populate() {
		this.clear();

		var headerZoneLabel = new WidgetTextBox(HEADER_LABEL_ZONE.get());
		headerZoneLabel.setTextColor(0xFFFFFFFF);
		headerZoneLabel.setFont(ModFonts.NOKIA);
		headerZoneLabel.autoWidth(150);
		headerZoneLabel.autoHeight();
		this.add(0, 0, new CellData(headerZoneLabel, ContentAlignment.MIDDLE_LEFT));

		var headerDeviceLabel = new WidgetTextBox(HEADER_LABEL_DEVICE.get());
		headerDeviceLabel.setTextColor(0xFFFFFFFF);
		headerDeviceLabel.setFont(ModFonts.NOKIA);
		headerDeviceLabel.autoWidth(200);
		headerDeviceLabel.autoHeight();
		this.add(1, 0, new CellData(headerDeviceLabel, ContentAlignment.MIDDLE_LEFT));

		var headerBlockStateLabel = new WidgetTextBox(HEADER_LABEL_BLOCK_STATE.get());
		headerBlockStateLabel.setTextColor(0xFFFFFFFF);
		headerBlockStateLabel.setFont(ModFonts.NOKIA);
		headerBlockStateLabel.autoWidth(150);
		headerBlockStateLabel.autoHeight();
		this.add(2, 0, new CellData(headerBlockStateLabel, ContentAlignment.MIDDLE_LEFT));

		var headerSpecificLabel = new WidgetTextBox(HEADER_LABEL_SPECIFIC_SENSORS.get());
		headerSpecificLabel.setTextColor(0xFFFFFFFF);
		headerSpecificLabel.setFont(ModFonts.NOKIA);
		headerSpecificLabel.autoWidth(150);
		headerSpecificLabel.autoHeight();

		var genericFilterToggle = new WidgetToggle(this.hideGenericDevices);
		genericFilterToggle.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				this.hideGenericDevices = (boolean) event.newValue;
				this.populate();
				return WidgetEventResult.CONTINUE_PROCESSING;
			});
		genericFilterToggle.setTooltipElements(WrappedStringTooltipComponent.gray(TOOLTIP_SHOW_SPECIFIC_ONLY.get()));

		var sensorHBox = new WidgetHBox();
		sensorHBox.setSpacing(5);
		sensorHBox.setWidth(headerSpecificLabel.width() + genericFilterToggle.width() + 5);
		sensorHBox.setHeight(Math.max(headerSpecificLabel.height(), genericFilterToggle.height()));
		sensorHBox.addContentBox(headerSpecificLabel);
		sensorHBox.addContentBox(genericFilterToggle);

		this.add(3, 0, new CellData(sensorHBox, ContentAlignment.MIDDLE_LEFT));

		var headerRestoreLabel = new WidgetTextBox(HEADER_LABEL_RESTORE.get());
		headerRestoreLabel.setTextColor(0xFFFFFFFF);
		headerRestoreLabel.setFont(ModFonts.NOKIA);
		headerRestoreLabel.autoWidth(50);
		headerRestoreLabel.autoHeight();
		this.add(4, 0, new CellData(headerRestoreLabel, ContentAlignment.MIDDLE_CENTER));



		List<HomeZone> zones = allConfiguredDevices.keySet().stream().sorted(Comparator.comparing(HomeZone::name)).toList();
		int row = 1;
		for(var zone : zones) {
			var devices = allConfiguredDevices.get(zone);
			for(var device : devices) {
				if(!device.ignored()) {
					continue;
				}

				var specificSensors = device.sensors().keySet().stream().map(ModSensors::getById).filter(Predicate.not(HomeSensor::isGeneric)).toList();
				int specificSensorCount = specificSensors.size();

				if(this.hideGenericDevices && specificSensorCount == 0) {
					continue;
				}

				var zoneLabel = new WidgetTextBox(zone.name());
				zoneLabel.setTextColor(0xFFAAAAAA);
				zoneLabel.autoWidth(150);
				zoneLabel.autoHeight();
				this.add(0, row, new CellData(zoneLabel, ContentAlignment.MIDDLE_LEFT));

				var deviceLabel = new WidgetTextBox(device.name());
				deviceLabel.setTextColor(0xFFAAAAAA);
				deviceLabel.autoWidth(200);
				deviceLabel.autoHeight();
				this.add(1, row, new CellData(deviceLabel, ContentAlignment.MIDDLE_LEFT));

				var worldInfo = DashboardScreen.get().getMenu().homeWorldInfo;
				var blockState = worldInfo.blockStates().get(device.pos());
				if(blockState == null) {
					blockState = Blocks.AIR.defaultBlockState();
				}

				var blockStateLabel = new WidgetTextBox(I18n.get(blockState.getBlock().getDescriptionId()));
				blockStateLabel.setTextColor(0xFFAAAAAA);
				blockStateLabel.autoWidth(50);
				blockStateLabel.autoHeight();
				this.add(2, row, new CellData(blockStateLabel, ContentAlignment.MIDDLE_LEFT));

				var specificLabel = new WidgetTextBox("" + specificSensorCount);
				specificLabel.setTextColor(0xFFAAAAAA);
				specificLabel.autoWidth(50);
				specificLabel.autoHeight();
				specificLabel.setTooltipElements(
					specificSensors.stream().map(s -> WrappedStringTooltipComponent.gray(s.getDisplayName().get())).toArray(TooltipComponent[]::new)
				);
				this.add(3, row, new CellData(specificLabel, ContentAlignment.MIDDLE_CENTER));

				var restoreButton = new WidgetSprite(HackerNoon.Regular.refresh);
				restoreButton.setColor(0xFFAAAAAA, ColorHelper.COLOR_ORANGE);
				restoreButton.setScale(0.5f);
				restoreButton.addListener(MouseClickEvent.class, (event, widget) -> {
					PacketDistributor.sendToServer(new SetDeviceIgnorePayload(zone.home().id(), zone.id(), device, false));
					return WidgetEventResult.HANDLED;
				});
				this.add(4, row, new CellData(restoreButton, ContentAlignment.MIDDLE_CENTER));

				row++;
			}
		}

		if(this.getParent() != null) {
			this.getParent().getParent().updateWidgetSizes();
		}
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();

		int yPos = 0;
		for(int row = 0; row < this.getRowCount(); row++) {
			int labelMaxWidths = this.width() - this.getColumnWidth(1) - 2 * this.paddingHorizontal() - 40;
			int rowHeight = 0;
			if(this.get(0, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
				rowHeight = Math.max(rowHeight, desc.getHeight());
			}
			if(this.get(1, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
				rowHeight = Math.max(rowHeight, desc.getHeight());
			}
			if(this.get(2, row).widget() instanceof WidgetTextBox desc) {
				desc.autoWidth(Math.max(150, labelMaxWidths / 2));
				desc.autoHeight();
				rowHeight = Math.max(rowHeight, desc.getHeight());
			}
			yPos += rowHeight + this.paddingVertical() + 2;
		}

		this.setHeight(yPos + 12);


	}
}
