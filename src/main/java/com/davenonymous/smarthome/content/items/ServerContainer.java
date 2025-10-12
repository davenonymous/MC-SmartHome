package com.davenonymous.smarthome.content.items;

import com.davenonymous.smarthome.lib.gui.WidgetContainer;
import com.davenonymous.smarthome.setup.content.ModContainers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ServerContainer extends WidgetContainer {
	public static int WIDTH = 176;
	public static int HEIGHT = 145;

	public ServerContainer(int id, Inventory inv, @NotNull Player player) {
		super(ModContainers.SERVER_CONTAINER.get(), id, inv);

		this.layoutPlayerInventorySlots(8, HEIGHT - 84);

	}

	@Override
	public boolean stillValid(Player pPlayer) {
		ItemStack mainHand = pPlayer.getMainHandItem();
		if (!(mainHand.getItem() instanceof ServerItem)) {
			return false;
		}

		return super.stillValid(pPlayer);
	}
}
