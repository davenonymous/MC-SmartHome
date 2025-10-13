package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.content.blocks.projector.ProjectorContainer;
import com.davenonymous.smarthome.gui.home.NoHomesWidget;
import com.davenonymous.smarthome.gui.projector.ProjectorSettingsPanel;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetContainerScreen;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetCloseButton;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ProjectorScreen extends WidgetContainerScreen<ProjectorContainer> {

	ProjectorSettingsPanel settingsPanel;

	public ProjectorScreen(ProjectorContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.renderTitle = false;
		this.renderInventoryTitle = false;
	}



	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, ProjectorContainer.WIDTH, ProjectorContainer.HEIGHT);
		gui.setContainer(this.menu);

		recreateGui(gui);

		return gui;
	}

	private void recreateGui(GUI gui) {
		gui.clear();

		WidgetCloseButton closeButton = new WidgetCloseButton();
		closeButton.setPosition(gui.width - 14, 4);
		gui.add(closeButton);

		WidgetTextBox title = new WidgetTextBox(I18n.get("block.smarthome.projector"));
		title.setPosition(8, 8);
		title.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		title.autoWidth();
		title.autoHeight();
		gui.add(title);

		if(this.menu.ownedHomes().isEmpty()) {
			var noHomesWidget = new NoHomesWidget();
			gui.add(noHomesWidget);
			noHomesWidget.updateWidgetSizes();
			return;
		}

		settingsPanel = new ProjectorSettingsPanel(menu);
		settingsPanel.setPosition(8, 24);
		settingsPanel.setWidth(gui.width - 16);
		settingsPanel.addListener(ValueChangedEvent.class, (event, widget) -> {
			recreateGui(gui);
			return WidgetEventResult.HANDLED;
		});

		gui.add(settingsPanel);
	}


}
