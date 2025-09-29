package com.davenonymous.smarthome.gui.home.main.settings;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeSettings;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.networking.actions.SetHomeSettingsPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class SettingsContainer extends WidgetPanel {
	SettingsBox generalBox;
	WidgetToggle autoEnableNewDevicesToggle;
	WidgetToggle autoIgnoreGenericOnlyDevicesToggle;
	WidgetToggle renameBlocksToDeviceNamesToggle;

	@I18DataGen(lang = "en_us", string = "General")
	@I18DataGen(lang = "de_de", string = "Allgemein")
	public static final I18String BOX_TITLE = SmartHome.guiString("settings", "general");

	@I18DataGen(lang = "en_us", string = "Automatically enable new devices")
	@I18DataGen(lang = "de_de", string = "Neue Geräte automatisch aktivieren")
	public static final I18String AUTO_ENABLE_NEW_DEVICES = SmartHome.guiString("settings", "auto_enable_new_devices");

	@I18DataGen(lang = "en_us", string = "When you add a newly found device to one of your zones, it and all non-generic sensors it provides will be enabled immediately.")
	@I18DataGen(lang = "de_de", string = "Wenn Sie ein neu gefundenes Gerät zu einer Ihrer Zonen hinzufügen, wird es und alle nicht generischen Sensoren, die es bereitstellt, sofort aktiviert.")
	public static final I18String AUTO_ENABLE_NEW_DEVICES_DESCRIPTION = SmartHome.guiString("settings", "auto_enable_new_devices_description");

	@I18DataGen(lang = "en_us", string = "Automatically ignore new generic devices")
	@I18DataGen(lang = "de_de", string = "Neue generische Geräte automatisch ignorieren")
	public static final I18String AUTO_IGNORE_GENERIC_ONLY_DEVICES = SmartHome.guiString("settings", "auto_ignore_generic_only_devices");

	@I18DataGen(lang = "en_us", string = "When a new device has been found that only provides generic sensors, it will be automatically added to the list of ignored devices.")
	@I18DataGen(lang = "de_de", string = "Wenn ein neues Gerät gefunden wurde, das nur generische Sensoren bereitstellt, wird es automatisch zur Liste der ignorierten Geräte hinzugefügt.")
	public static final I18String AUTO_IGNORE_GENERIC_ONLY_DEVICES_DESCRIPTION = SmartHome.guiString("settings", "auto_ignore_generic_only_devices_description");

	@I18DataGen(lang = "en_us", string = "Rename blocks when renaming devices")
	@I18DataGen(lang = "de_de", string = "Blöcke beim Umbenennen von Geräten umbenennen")
	public static final I18String RENAME_BLOCKS_TO_DEVICE_NAMES = SmartHome.guiString("settings", "rename_blocks_to_device_names");

	@I18DataGen(lang = "en_us", string = "Blocks that can have a custom name will automatically be renamed to match their device name.")
	@I18DataGen(lang = "de_de", string = "Blöcke, die einen benutzerdefinierten Namen haben können, werden automatisch umbenannt, um mit ihrem Gerätenamen übereinzustimmen.")
	public static final I18String RENAME_BLOCKS_TO_DEVICE_NAMES_DESCRIPTION = SmartHome.guiString("settings", "rename_blocks_to_device_names_description");

	public SettingsContainer() {
		generalBox = new SettingsBox(BOX_TITLE.get());
		generalBox.setPosition(5, 5);
		this.add(generalBox);

		var home = HomeScreen.get().selectedHome;
		var table = generalBox.getSettingsTable();
		table.addListener(ValueChangedEvent.class, (event, widget) -> {
			var newHomeSettings = new HomeSettings(
				renameBlocksToDeviceNamesToggle.getValue(),
				autoIgnoreGenericOnlyDevicesToggle.getValue(),
				autoEnableNewDevicesToggle.getValue()
			);
			home.setSettings(newHomeSettings);
			PacketDistributor.sendToServer(new SetHomeSettingsPayload(home.id(), newHomeSettings));
			return WidgetEventResult.CONTINUE_PROCESSING;
		});

		var settings = home.settings();

		autoEnableNewDevicesToggle = new WidgetToggle(settings.autoEnableNewDevices());
		table.addSetting(
			AUTO_ENABLE_NEW_DEVICES.get(), autoEnableNewDevicesToggle,
			AUTO_ENABLE_NEW_DEVICES_DESCRIPTION.get(), widget -> WrappedStringTooltipComponent.red("Enter your username here.")
		);

		autoIgnoreGenericOnlyDevicesToggle = new WidgetToggle(settings.autoIgnoreGenericOnlyDevices());
		table.addSetting(
			AUTO_IGNORE_GENERIC_ONLY_DEVICES.get(), autoIgnoreGenericOnlyDevicesToggle,
			AUTO_IGNORE_GENERIC_ONLY_DEVICES_DESCRIPTION.get(), widget -> WrappedStringTooltipComponent.red("Enter your username here.")
		);

		renameBlocksToDeviceNamesToggle = new WidgetToggle(settings.renameBlocksToDeviceNames());
		table.addSetting(
			RENAME_BLOCKS_TO_DEVICE_NAMES.get(), renameBlocksToDeviceNamesToggle,
			RENAME_BLOCKS_TO_DEVICE_NAMES_DESCRIPTION.get(), widget -> WrappedStringTooltipComponent.red("Enter your username here.")
		);

		updateWidgetSizes();
	}

	@Override
	public void updateWidgetSizes() {
		super.updateWidgetSizes();
		generalBox.setSize(this.width - 10, this.height - 10);
		generalBox.updateWidgetSizes();
	}
}
