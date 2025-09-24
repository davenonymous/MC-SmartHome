package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.gui.events.ContentSelectionEvent;
import com.davenonymous.smarthome.gui.home.main.devices.DevicesContainer;
import com.davenonymous.smarthome.gui.home.main.zones.ZonesContainer;
import com.davenonymous.smarthome.lib.gui.GuiTheme;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetPanel;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ContentContainerWidget extends WidgetPanel {
	private Map<ResourceLocation, Widget> registeredContentWidgets;

	private ResourceLocation activeContentId;
	private Widget activeContentWidget;

	public ContentContainerWidget() {
		registeredContentWidgets = new HashMap<>();

		registerContentWidget(ContentIDs.ZONES, new ZonesContainer());
		registerContentWidget(ContentIDs.DEVICES, new DevicesContainer());

		this.addListener(ContentSelectionEvent.class, (event, widget) -> {
			setActiveContentWidget(event.contentId());
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public ContentContainerWidget registerContentWidget(ResourceLocation id, Widget widget) {
		widget.setVisible(false);

		if(registeredContentWidgets.isEmpty()) {
			activeContentWidget = widget;
			activeContentId = id;
			widget.setVisible(true);
		}
		registeredContentWidgets.put(id, widget);
		this.add(widget);
		return this;
	}

	public ContentContainerWidget setActiveContentWidget(ResourceLocation id) {
		if(!registeredContentWidgets.containsKey(id)) {
			return this;
		}

		if(activeContentWidget != null) {
			activeContentWidget.setVisible(false);
		}

		activeContentWidget = registeredContentWidgets.get(id);
		activeContentId = id;
		activeContentWidget.setVisible(true);

		updateWidgetSizes();
		return this;
	}

	public void updateWidgetSizes() {
		registeredContentWidgets.values().forEach(widget -> {
			widget.setSize(this.width-10, this.height-10);
			widget.setPosition(5, 5);
			widget.updateWidgetSizes();
		});
	}

	@Override
	public void draw(GuiGraphics guiGraphics, Window window) {
		guiGraphics.blitSprite(SmartHome.sprite(GuiTheme.SpriteComponent.WINDOW_PUSHED_BACKGROUND), 0, 0, this.width, this.height);
		super.draw(guiGraphics, window);
	}
}
