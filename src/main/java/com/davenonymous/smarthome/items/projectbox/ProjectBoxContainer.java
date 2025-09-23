package com.davenonymous.smarthome.items.projectbox;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.WidgetContainer;
import com.davenonymous.smarthome.setup.content.ModContainers;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ProjectBoxContainer extends WidgetContainer {
	public static int WIDTH = 176;
	public static int HEIGHT = 195;

	public static ResourceLocation SLOTGROUP_MICROCONTROLLER = SmartHome.resource("microcontroller");
	public static ResourceLocation SLOTGROUP_PERIPHERALS = SmartHome.resource("peripherals");

	public ProjectBoxContainer(int id, Inventory inv, @NotNull Player player) {
		super(ModContainers.PROJECT_BOX_CONTAINER.get(), id, inv);
		this.layoutPlayerInventorySlots(8, HEIGHT - 84);

		var projectBoxStack = player.getMainHandItem();
		if(!(projectBoxStack.getItem() instanceof ProjectBoxItem projectBoxItem)) {
			return;
		}

		ProjectBoxDataComponent data = projectBoxStack.get(ModDataComponents.PROJECT_BOX_DATA_COMPONENT);
		if(data == null) {
			data = new ProjectBoxDataComponent();
		}

		var handler = data.getInventoryHandler(projectBoxStack);
		this.addSlotRange(SLOTGROUP_MICROCONTROLLER, handler, 0, 20, 52, 1, 18);
		this.addSlotRange(SLOTGROUP_PERIPHERALS, handler, 1, 50, 52, 4, 18);

	}

	@Override
	public boolean stillValid(Player pPlayer) {
		ItemStack mainHand = pPlayer.getMainHandItem();
		if (!(mainHand.getItem() instanceof ProjectBoxItem)) {
			return false;
		}

		return super.stillValid(pPlayer);
	}
}
