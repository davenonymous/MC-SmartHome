package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.home.HeaderWidget;
import com.davenonymous.smarthome.gui.home.NoHomesWidget;
import com.davenonymous.smarthome.gui.home.SidebarWidget;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetFullScreen;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class HomeScreen extends WidgetFullScreen {
	WidgetVBox mainLayout;
	HeaderWidget headerLayout;
	WidgetHBox contentLayout;
	WidgetVBox footerLayout;
	NoHomesWidget noHomesWidget;
	SidebarWidget sidebarWidget;

	public HomeCore selectedHome;
	public HomeBlockEntity blockEntity;
	public List<HomeCore> ownedHomes;

	public HomeScreen(BlockPos pos, UUID selectedHomeId, List<HomeCore> ownedHomes) {
		super(Component.translatable("smarthome.gui.home.title"));
		this.ownedHomes = ownedHomes;

		if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof HomeBlockEntity hbe) {
			this.blockEntity = hbe;
		}

		for(var home : ownedHomes) {
			if(home.id().equals(selectedHomeId)) {
				this.selectedHome = home;
				break;
			}
		}

		if(this.selectedHome == null && !ownedHomes.isEmpty()) {
			this.selectedHome = ownedHomes.getFirst();
		}

		SmartHome.LOGGER.debug("Opening home screen for home {} from {}", selectedHome, blockEntity);
	}

	@Override
	protected GUI createGUI() {
		var gui = super.createGUI();

		mainLayout = new WidgetVBox();
		mainLayout.setSpacing(2);
		mainLayout.setPadding(6);

		headerLayout = new HeaderWidget(this);
		headerLayout.setSpacing(2);
		headerLayout.setPadding(0);

		contentLayout = new WidgetHBox();
		contentLayout.setSpacing(2);
		contentLayout.setPadding(0);

		//contentLayout.addContentBox(new FontTestWidget(), FlexSizer.FlexAlign.START);

		footerLayout = new WidgetVBox();
		footerLayout.setSpacing(2);
		footerLayout.setPadding(0);

		mainLayout.addContentBox(headerLayout);
		mainLayout.addContentBox(contentLayout);
		mainLayout.addContentBox(footerLayout);

		noHomesWidget = new NoHomesWidget(this);
		sidebarWidget = new SidebarWidget(this);

		gui.add(mainLayout);

		updateWidgetSizes();

		if(this.ownedHomes.isEmpty()) {
			contentLayout.addFlexBox(new Widget(), 1);
			contentLayout.addFlexBox(noHomesWidget, FlexSizer.FlexAlign.CENTER, 2);
			contentLayout.addFlexBox(new Widget(), 1);
			noHomesWidget.updateWidgetSizes();
		} else {
			contentLayout.addContentBox(sidebarWidget, FlexSizer.FlexAlign.FILL);
		}

		return gui;
	}

	@Override
	protected void updateWidgetSizes() {
		mainLayout.setWidth(this.width);
		mainLayout.setHeight(this.height);
		mainLayout.setPosition(0, 0);

		headerLayout.setWidth(mainLayout.width);
		headerLayout.setHeight(20);
		headerLayout.updateWidgetSizes();

		footerLayout.setWidth(mainLayout.width);
		footerLayout.setHeight(20);

		contentLayout.setWidth(mainLayout.width - mainLayout.padding*2);
		contentLayout.setX(mainLayout.padding);
		contentLayout.setHeight(mainLayout.height - headerLayout.height - footerLayout.height - mainLayout.padding*2 - mainLayout.spacing*2);

		contentLayout.update(null);
		noHomesWidget.updateWidgetSizes();
	}
}
