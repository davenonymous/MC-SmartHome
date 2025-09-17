package com.davenonymous.smarthome.gui;

import com.davenonymous.smarthome.blocks.MiniRackContainer;
import com.davenonymous.smarthome.lib.gui.GUI;
import com.davenonymous.smarthome.lib.gui.WidgetContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MiniRackScreen extends WidgetContainerScreen<MiniRackContainer> {
	public MiniRackScreen(MiniRackContainer container, Inventory inv, Component name) {
		super(container, inv, name);
	}

	@Override
	protected GUI createGUI() {
		GUI gui = new GUI(0, 0, MiniRackContainer.WIDTH, MiniRackContainer.HEIGHT);
		gui.setContainer(this.menu);

		return gui;
	}
}
