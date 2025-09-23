package com.davenonymous.smarthome.gui.home.sidebar;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.home.ContentIDs;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.ColorHelper;
import com.davenonymous.smarthome.lib.gui.widgets.layout.Spacer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;

public class SidebarWidget extends WidgetVBox {
	SidebarButton zonesButton;
	SidebarButton devicesButton;
	SidebarButton settingsButton;

	public SidebarWidget(HomeScreen screen) {
		this.setWidth(120);
		this.setPadding(1);
		this.setSpacing(4);

		this.zonesButton = new SidebarButton(HackerNoon.Solid.home, I18n.get("smarthome.gui.home.sidebar.zones"));
		this.zonesButton.setContentId(ContentIDs.ZONES);
		this.addContentBox(zonesButton);

		this.devicesButton = new SidebarButton(HackerNoon.Solid.retroCamera, I18n.get("smarthome.gui.home.sidebar.devices"));
		this.devicesButton.setContentId(ContentIDs.DEVICES);
		var newDeviceCount = screen.newDevices.size();
		if(newDeviceCount > 0) {
			this.devicesButton.setBadge(
				Integer.toString(newDeviceCount),
				I18n.get("smarthome.gui.home.sidebar.devices.badge", newDeviceCount),
				ColorHelper.COLOR_GREEN, ChatFormatting.WHITE.getColor());
		}
		this.addContentBox(devicesButton);

		this.addFlexBox(new Spacer(100, 1), FlexAlign.START, 1);

		this.settingsButton = new SidebarButton(HackerNoon.Solid.cog, I18n.get("smarthome.gui.home.sidebar.settings"));
		this.addContentBox(settingsButton);
	}

}
