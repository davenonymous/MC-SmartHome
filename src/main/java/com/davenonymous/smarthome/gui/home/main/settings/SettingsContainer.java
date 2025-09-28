package com.davenonymous.smarthome.gui.home.main.settings;

import com.davenonymous.smarthome.data.HomeSettings;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.general.WidgetToggle;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.davenonymous.smarthome.networking.actions.SetHomeSettingsPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class SettingsContainer extends WidgetPanel {
	SettingsBox generalBox;
	WidgetToggle autoEnableNewDevicesToggle;
	WidgetToggle autoIgnoreGenericOnlyDevicesToggle;
	WidgetToggle renameBlocksToDeviceNamesToggle;

	public SettingsContainer() {
		generalBox = new SettingsBox("General");
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
			"Automatically enable new devices", autoEnableNewDevicesToggle,
			"When you add a newly found device to one of your zones, it and all non-generic sensors it provides will be enabled immediately.", widget -> WrappedStringTooltipComponent.red("Enter your username here.")
		);

		autoIgnoreGenericOnlyDevicesToggle = new WidgetToggle(settings.autoIgnoreGenericOnlyDevices());
		table.addSetting(
			"Automatically ignore new generic devices", autoIgnoreGenericOnlyDevicesToggle,
			"When a new device has been found that only provides generic sensors, it will be automatically added to the list of ignored devices.", widget -> WrappedStringTooltipComponent.red("Enter your username here.")
		);

		renameBlocksToDeviceNamesToggle = new WidgetToggle(settings.renameBlocksToDeviceNames());
		table.addSetting(
			"Rename blocks when renaming devices", renameBlocksToDeviceNamesToggle,
			"Blocks that can have a custom name will automatically be renamed to match their device name.", widget -> WrappedStringTooltipComponent.red("Enter your username here.")
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
