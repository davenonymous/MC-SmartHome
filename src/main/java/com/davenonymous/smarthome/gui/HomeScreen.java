package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetFullScreen;
import com.davenonymous.smarthome.lib.gui.widgets.layout.FlexSizer;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetHBox;
import com.davenonymous.smarthome.lib.gui.widgets.layout.WidgetVBox;
import net.minecraft.network.chat.Component;

public class HomeScreen extends WidgetFullScreen {
	WidgetVBox mainLayout;
	WidgetHBox headerLayout;
	WidgetHBox contentLayout;
	WidgetVBox footerLayout;

	public HomeScreen() {
		super(Component.translatable("smarthome.gui.home.title"));
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
