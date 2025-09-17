package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetFullScreen;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.networking.ClientCache;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class HomeScreen extends WidgetFullScreen {
	WidgetVBox mainLayout;
	WidgetHBox headerLayout;
	WidgetHBox contentLayout;
	WidgetVBox footerLayout;

	HomeCore selectedHome;
	HomeBlockEntity blockEntity;

	public HomeScreen(BlockPos pos, UUID selectedHomeId) {
		super(Component.translatable("smarthome.gui.home.title"));

		if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof HomeBlockEntity hbe) {
			this.blockEntity = hbe;
		}

		for(var home : ClientCache.getOwnedHomes()) {
			if(home.id().equals(selectedHomeId)) {
				this.selectedHome = home;
				break;
			}
		}

		SmartHome.LOGGER.debug("Opening home screen for home {} from {}", selectedHome, blockEntity);
	}

	@Override
	protected GUI createGUI() {
		var gui = super.createGUI();

		mainLayout = new WidgetVBox();
		mainLayout.setSpacing(2);
		mainLayout.setPadding(4);

		headerLayout = new WidgetHBox();
		headerLayout.setSpacing(2);
		headerLayout.setPadding(0);

		contentLayout = new WidgetHBox();
		contentLayout.setSpacing(2);
		contentLayout.setPadding(0);

		footerLayout = new WidgetVBox();
		footerLayout.setSpacing(2);
		footerLayout.setPadding(0);

		mainLayout.addContentBox(headerLayout);
		mainLayout.addContentBox(contentLayout, WidgetVBox.FlexAlign.FILL);
		mainLayout.addContentBox(footerLayout);

		updateWidgetSizes();
		return gui;
	}

	@Override
	protected void updateWidgetSizes() {
		mainLayout.setWidth(this.width);
		mainLayout.setHeight(this.height);
		mainLayout.setPosition(0, 0);

		headerLayout.setWidth(mainLayout.width);
		headerLayout.setHeight(40);

		footerLayout.setWidth(mainLayout.width);
		footerLayout.setHeight(40);

		contentLayout.setWidth(mainLayout.width);
		contentLayout.setHeight(mainLayout.height - headerLayout.height - footerLayout.height);
	}
}
