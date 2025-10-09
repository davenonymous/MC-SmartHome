package com.davenonymous.smarthome.gui.home.sidebar;

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

public class SidebarWidget extends WidgetVBox {
	SidebarButton zonesButton;
	SidebarButton devicesButton;
	SidebarButton dashboardsButton;
	SidebarButton cardsButton;
	SidebarButton settingsButton;

	@I18DataGen(lang = "en_us", string = "Zones")
	@I18DataGen(lang = "de_de", string = "Zonen")
	public static final I18String ZONES = I18String.gui("home.sidebar", "zones");

	@I18DataGen(lang = "en_us", string = "Devices")
	@I18DataGen(lang = "de_de", string = "Geräte")
	public static final I18String DEVICES = I18String.gui("home.sidebar", "devices");

	@I18DataGen(lang = "en_us", string = "Dashboards")
	@I18DataGen(lang = "de_de", string = "Dashboards")
	public static final I18String DASHBOARDS = I18String.gui("home.sidebar", "dashboards");

	@I18DataGen(lang = "en_us", string = "Cards")
	@I18DataGen(lang = "de_de", string = "Karten")
	public static final I18String CARDS = I18String.gui("home.sidebar", "cards");

	@I18DataGen(lang = "en_us", string = "Settings")
	@I18DataGen(lang = "de_de", string = "Einstellungen")
	public static final I18String SETTINGS = I18String.gui("home.sidebar", "settings");

	@I18DataGen(lang = "en_us", string = "New devices found: %d")
	@I18DataGen(lang = "de_de", string = "Neue Geräte gefunden: %d")
	public static final I18String NEW_DEVICES = I18String.gui("home.sidebar", "devices.badge");

	public SidebarWidget(HomeScreen screen) {
		this.setWidth(120);
		this.setPadding(1);
		this.setSpacing(4);

		this.zonesButton = new SidebarButton(HackerNoon.Solid.home, ZONES.get());
		this.zonesButton.setContentId(ContentIDs.ZONES);
		this.zonesButton.setActive(true);
		this.addContentBox(zonesButton);

		this.devicesButton = new SidebarButton(HackerNoon.Solid.retroCamera, DEVICES.get());
		this.devicesButton.setContentId(ContentIDs.DEVICES);
		updateNewDeviceCount();
		this.addContentBox(devicesButton);

		this.addFlexBox(new Spacer(100, 1), FlexAlign.START, 1);

		this.dashboardsButton = new SidebarButton(HackerNoon.Solid.table, DASHBOARDS.get());
		this.dashboardsButton.setContentId(ContentIDs.DASHBOARDS);
		this.addContentBox(dashboardsButton);

		this.cardsButton = new SidebarButton(HackerNoon.Solid.bolt, CARDS.get());
		this.cardsButton.setContentId(ContentIDs.CARDS);
		this.addContentBox(cardsButton);

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
