package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.content.blocks.dashboard.DashboardBlockEntity;
import com.davenonymous.smarthome.content.blocks.dashboard.DashboardContainer;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.gui.home.ContentContainerWidget;
import com.davenonymous.smarthome.gui.home.HeaderWidget;
import com.davenonymous.smarthome.gui.home.NoHomesWidget;
import com.davenonymous.smarthome.gui.home.sidebar.SidebarWidget;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetContainerFullScreen;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.lib.i18n.I18String;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardScreen extends WidgetContainerFullScreen<DashboardContainer> {
	WidgetVBox mainLayout;
	HeaderWidget headerLayout;
	WidgetHBox contentLayout;
	WidgetVBox footerLayout;
	NoHomesWidget noHomesWidget;
	SidebarWidget sidebarWidget;
	ContentContainerWidget contentContainerWidget;

	public HomeCore selectedHome;
	public DashboardBlockEntity blockEntity;

	private Map<String, Object> screenState;



	@I18DataGen(lang = "en_us", string = "Smart Home")
	@I18DataGen(lang = "de_de", string = "Smart Home")
	public static final I18String TITLE = I18String.gui("home", "title");

	public DashboardScreen(DashboardContainer container, Inventory inv, Component name) {
		super(container, inv, Component.translatable(TITLE.key()));
		this.screenState = new HashMap<>();
		this.blockEntity = container.getBlockEntity();
		this.renderTitle = false;
		this.renderInventoryTitle = false;

		for(var home : container.ownedHomes) {
			if(home.id().equals(blockEntity.home())) {
				this.selectedHome = home;
				break;
			}
		}
		if(this.selectedHome == null && !container.ownedHomes.isEmpty()) {
			this.selectedHome = container.ownedHomes.getFirst();
		}
	}

	public <T> T getScreenState(String key) {
		//noinspection unchecked
		return (T)screenState.get(key);
	}

	public <T> void setScreenState(String key, T value) {
		screenState.put(key, value);
	}



	public static DashboardScreen get() {
		var mc = Minecraft.getInstance();
		if(mc.screen instanceof DashboardScreen dashboardScreen) {
			return dashboardScreen;
		}

		return null;
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

		contentContainerWidget = new ContentContainerWidget();
		//contentLayout.addContentBox(new FontTestWidget(), FlexSizer.FlexAlign.START);

		footerLayout = new WidgetVBox();
		footerLayout.setSpacing(2);
		footerLayout.setPadding(0);

		mainLayout.addContentBox(headerLayout);
		mainLayout.addContentBox(contentLayout);
		mainLayout.addContentBox(footerLayout);

		noHomesWidget = new NoHomesWidget();
		sidebarWidget = new SidebarWidget(this);

		gui.add(mainLayout);

		updateWidgetSizes();

		if(this.menu.ownedHomes.isEmpty()) {
			contentLayout.addFlexBox(new Widget(), 1);
			contentLayout.addFlexBox(noHomesWidget, FlexSizer.FlexAlign.CENTER, 2);
			contentLayout.addFlexBox(new Widget(), 1);
			noHomesWidget.updateWidgetSizes();
		} else {
			contentLayout.addContentBox(sidebarWidget, FlexSizer.FlexAlign.FILL);
			contentLayout.addContentBox(contentContainerWidget, FlexSizer.FlexAlign.FILL);
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

		contentLayout.setWidth(mainLayout.width - mainLayout.paddingHorizontal*2);
		contentLayout.setX(mainLayout.paddingHorizontal);
		contentLayout.setHeight(mainLayout.height - headerLayout.height - footerLayout.height - mainLayout.paddingVertical*2 - mainLayout.spacing*2);

		contentLayout.update(null);
		noHomesWidget.updateWidgetSizes();

		contentContainerWidget.setWidth(contentLayout.width - sidebarWidget.width - contentLayout.spacing);
		contentContainerWidget.setHeight(contentLayout.height);
		contentContainerWidget.updateWidgetSizes();
	}

	public Map<HomeZone, List<FoundDevice>> getAllNewDevices() {
		if(selectedHome == null) {
			return Map.of();
		}
		return selectedHome.getAllFoundDevices();
	}
}
