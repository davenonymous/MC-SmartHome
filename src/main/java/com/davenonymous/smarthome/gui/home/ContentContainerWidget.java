package com.davenonymous.smarthome.gui.home;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.dashboard.DashboardBlockEntity;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.ContentSelectionEvent;
import com.davenonymous.smarthome.gui.home.main.cards.CardEditorContainer;
import com.davenonymous.smarthome.gui.home.main.dashboard.DashboardContainer;
import com.davenonymous.smarthome.gui.home.main.devices.DevicesContainer;
import com.davenonymous.smarthome.gui.home.main.settings.SettingsContainer;
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
import java.util.function.Supplier;

public class ContentContainerWidget extends WidgetPanel {
	private Map<ResourceLocation, Supplier<Widget>> registeredContentWidgets;
	private Map<ResourceLocation, Widget> createdContentWidgets;

	private ResourceLocation activeContentId;
	private Widget activeContentWidget;


	public ContentContainerWidget() {
		registeredContentWidgets = new HashMap<>();
		createdContentWidgets = new HashMap<>();

		registerContentWidget(ContentIDs.ZONES, ZonesContainer::new);
		registerContentWidget(ContentIDs.DEVICES, DevicesContainer::new);
		registerContentWidget(ContentIDs.SETTINGS, SettingsContainer::new);
		registerContentWidget(ContentIDs.DASHBOARDS, DashboardContainer::new);
		registerContentWidget(ContentIDs.CARDS, CardEditorContainer::new);

		this.addListener(ContentSelectionEvent.class, (event, widget) -> {
			setActiveContentWidget(event.contentId());
			if(HomeScreen.get().blockEntity instanceof DashboardBlockEntity dashy) {
				// TODO: Send payload to server, request change of default home screen page
			}
			return WidgetEventResult.CONTINUE_PROCESSING;
		});
	}

	public ContentContainerWidget registerContentWidget(ResourceLocation id, Supplier<Widget> widget) {
		boolean wasEmpty = registeredContentWidgets.isEmpty();
		registeredContentWidgets.put(id, widget);
		if(wasEmpty) {
			setActiveContentWidget(id);
		}
		return this;
	}

	public ContentContainerWidget setActiveContentWidget(ResourceLocation id) {
		if(!registeredContentWidgets.containsKey(id)) {
			return this;
		}

		var contentWidget = createdContentWidgets.computeIfAbsent(id, forId -> {
			var createdWidget = registeredContentWidgets.get(forId).get();
			this.add(createdWidget);
			return createdWidget;
		});

		if(activeContentWidget != null) {
			activeContentWidget.setVisible(false);
		}

		activeContentWidget = contentWidget;
		activeContentId = id;
		activeContentWidget.setVisible(true);

		updateWidgetSizes();
		return this;
	}

	public void updateWidgetSizes() {
		createdContentWidgets.values().forEach(widget -> {
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
