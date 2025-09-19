package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.HackerNoon;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;

public class SidebarWidget extends WidgetVBox {
	SidebarButton zonesButton;
	SidebarButton devicesButton;
	SidebarButton settingsButton;

	public SidebarWidget(HomeScreen screen) {
		this.setWidth(120);
		this.setPadding(1);
		this.setSpacing(4);

		this.zonesButton = new SidebarButton(HackerNoon.Solid.home, "Zones");
		this.zonesButton.setContentId(ContentIDs.ZONES);
		this.addContentBox(zonesButton);

		this.devicesButton = new SidebarButton(HackerNoon.Solid.retroCamera, "Devices");
		this.devicesButton.setContentId(ContentIDs.DEVICES);
		this.addContentBox(devicesButton);

		this.settingsButton = new SidebarButton(HackerNoon.Solid.cog, "Settings");
		this.addContentBox(settingsButton);
	}

}
