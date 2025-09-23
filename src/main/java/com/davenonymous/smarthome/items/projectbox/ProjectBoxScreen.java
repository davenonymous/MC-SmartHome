package com.davenonymous.smarthome.items.projectbox;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetContainerScreen;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetLabel;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.networking.actions.SetProjectBoxNamePayload;
import com.davenonymous.smarthome.networking.actions.SetServerItemHomeNamePayload;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ProjectBoxScreen extends WidgetContainerScreen<ProjectBoxContainer> {
	StringInputWidget projectNameInput;
	WidgetTextBox projectNameLabel;

	public ProjectBoxScreen(ProjectBoxContainer container, Inventory inv, Component name) {
		super(container, inv, name);
	}

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, ProjectBoxContainer.WIDTH, ProjectBoxContainer.HEIGHT);
		gui.setContainer(this.menu);

		var label = new WidgetLabel(I18n.get("item.smarthome.project_box"));
		label.setTextColor(0xFF3F3F3F);
		label.setPosition(8, 6);
		gui.add(label);

		var projectBoxStack = Minecraft.getInstance().player.getMainHandItem();
		if(!(projectBoxStack.getItem() instanceof ProjectBoxItem projectBoxItem)) {
			return gui;
		}

		ProjectBoxDataComponent data = projectBoxStack.get(ModDataComponents.PROJECT_BOX_DATA_COMPONENT);
		if(data == null) {
			data = new ProjectBoxDataComponent();
		}

		projectNameLabel = new WidgetTextBox(I18n.get("smarthome.gui.project_box.name_label"));
		projectNameLabel.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		projectNameLabel.autoWidth(gui.width - 16);
		projectNameLabel.setPosition(8, 20);

		projectNameInput = new StringInputWidget(data.name(), SmartHome.DEFAULT_NAMING_REGEX);
		projectNameInput.setWidth(gui.width - 16);
		projectNameInput.setPosition(8, 30);

		projectNameInput.addListener(
			ValueChangedEvent.class, (event, widget) -> {
				var newName = projectNameInput.getValue();
				if(newName.isEmpty()) {
					return WidgetEventResult.HANDLED;
				}

				PacketDistributor.sendToServer(new SetProjectBoxNamePayload(newName));
				return WidgetEventResult.HANDLED;
			});

		gui.add(projectNameLabel);
		gui.add(projectNameInput);

		return gui;
	}
}
