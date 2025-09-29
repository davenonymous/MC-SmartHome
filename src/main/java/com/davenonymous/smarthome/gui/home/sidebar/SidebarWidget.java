package com.davenonymous.smarthome.gui.home.sidebar;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.home.ContentIDs;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class SidebarWidget extends WidgetVBox {
	SidebarButton zonesButton;
	SidebarButton devicesButton;
	SidebarButton settingsButton;

	@I18DataGen(lang = "en_us", string = "Zones")
	@I18DataGen(lang = "de_de", string = "Zonen")
	public static final I18String ZONES = SmartHome.guiString("home.sidebar", "zones");

	@I18DataGen(lang = "en_us", string = "Devices")
	@I18DataGen(lang = "de_de", string = "Geräte")
	public static final I18String DEVICES = SmartHome.guiString("home.sidebar", "devices");

	@I18DataGen(lang = "en_us", string = "Settings")
	@I18DataGen(lang = "de_de", string = "Einstellungen")
	public static final I18String SETTINGS = SmartHome.guiString("home.sidebar", "settings");

	@I18DataGen(lang = "en_us", string = "New devices found: %d")
	@I18DataGen(lang = "de_de", string = "Neue Geräte gefunden: %d")
	public static final I18String NEW_DEVICES = SmartHome.guiString("home.sidebar", "devices.badge");

	public SidebarWidget(HomeScreen screen) {
		this.setWidth(120);
		this.setPadding(1);
		this.setSpacing(4);

		this.zonesButton = new SidebarButton(HackerNoon.Solid.home, ZONES.get());
		this.zonesButton.setContentId(ContentIDs.ZONES);
		this.addContentBox(zonesButton);

		this.devicesButton = new SidebarButton(HackerNoon.Solid.retroCamera, DEVICES.get());
		this.devicesButton.setContentId(ContentIDs.DEVICES);
		updateNewDeviceCount();
		this.addContentBox(devicesButton);

		this.addFlexBox(new Spacer(100, 1), FlexAlign.START, 1);

		this.settingsButton = new SidebarButton(HackerNoon.Solid.cog, SETTINGS.get());
		this.settingsButton.setContentId(ContentIDs.SETTINGS);
		this.addContentBox(settingsButton);

		this.addListener(GuiDataUpdatedEvent.class, (event, widget) -> {
			updateNewDeviceCount();
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	private void updateNewDeviceCount() {
		var home = HomeScreen.get().selectedHome;
		if(home == null) {
			this.devicesButton.clearBadge();
			return;
		}

		var count = home.getAllFoundDevices().values().stream().reduce(0, (a, b) -> a + b.size(), Integer::sum);
		if(count > 0) {
			this.devicesButton.setBadge(
				Integer.toString(count),
				NEW_DEVICES.get(count),
				ColorHelper.COLOR_GREEN, ChatFormatting.WHITE.getColor());
		} else {
			this.devicesButton.clearBadge();
		}
	}

}
