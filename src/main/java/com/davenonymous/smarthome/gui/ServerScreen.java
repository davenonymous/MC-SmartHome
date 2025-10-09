package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.items.ServerContainer;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetContainerScreen;
import com.davenonymous.smarthome.lib.gui.configurable.StringInputWidget;
import com.davenonymous.smarthome.lib.gui.event.ValueChangedEvent;
import com.davenonymous.smarthome.lib.gui.event.WidgetEventResult;
import com.davenonymous.smarthome.lib.gui.widgets.WidgetTextBox;
import com.davenonymous.smarthome.lib.i18n.I18String;
import com.davenonymous.smarthome.lib.i18n.I18DataGen;
import com.davenonymous.smarthome.networking.actions.SetServerItemHomeNamePayload;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

public class ServerScreen extends WidgetContainerScreen<ServerContainer> {
	StringInputWidget nameInput;
	WidgetTextBox titleLabel;
	WidgetTextBox homeNameLabel;

	@I18DataGen(lang = "en_us", string = "Name your smart home:")
	@I18DataGen(lang = "de_de", string = "Benennen Sie Ihr Smart Home:")
	public static final I18String HOME_NAME = I18String.gui("server", "name_label");

	public ServerScreen(ServerContainer container, Inventory inv, Component name) {
		super(container, inv, name);
	}

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, ServerContainer.WIDTH, ServerContainer.HEIGHT);
		gui.setContainer(this.menu);

		var player = Minecraft.getInstance().player;
		if(player == null) {
			return gui;
		}

		var serverItem = player.getMainHandItem();
		if(!(serverItem.getItem() instanceof ServerItem)) {
			return gui;
		}

		if(!serverItem.has(ModDataComponents.SERVER_DATA_COMPONENT)) {
			return gui;
		}

		ServerDataComponent data = serverItem.get(ModDataComponents.SERVER_DATA_COMPONENT);
		if(data == null) {
			return gui;
		}

		homeNameLabel = new WidgetTextBox(HOME_NAME.get());
		homeNameLabel.setTextColor(ChatFormatting.DARK_GRAY.getColor());
		homeNameLabel.autoWidth(gui.width - 16);
		homeNameLabel.setPosition(8, 6);

		nameInput = new StringInputWidget(data.name(), ".*");
		nameInput.setWidth(gui.width - 16);
		nameInput.setPosition(8, 18);

		nameInput.addListener(ValueChangedEvent.class, (event, widget) -> {
			var newName = nameInput.getValue();
			if(newName.isEmpty()) {
				return WidgetEventResult.HANDLED;
			}

			PacketDistributor.sendToServer(new SetServerItemHomeNamePayload(newName));
			return WidgetEventResult.HANDLED;
		});

		gui.add(homeNameLabel);
		gui.add(nameInput);

		return gui;
	}
}
