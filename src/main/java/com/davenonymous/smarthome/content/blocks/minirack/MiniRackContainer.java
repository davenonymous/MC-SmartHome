package com.davenonymous.smarthome.content.blocks.minirack;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.lib.gui.WidgetBlockEntityContainer;
import com.davenonymous.smarthome.setup.content.ModContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class MiniRackContainer extends WidgetBlockEntityContainer<MiniRackBlockEntity> {
	public static int WIDTH = 176;
	public static int HEIGHT = 185;

	public static ResourceLocation SLOTGROUP_RACKSLOTS = SmartHome.resource("rackslots");

	public MiniRackContainer(int id, BlockPos pos, Inventory inv, @NotNull Player player) {
		super(ModContainers.MINI_RACK_CONTAINER.get(), id, pos, inv, player);

		this.layoutPlayerInventorySlots(8, HEIGHT - 84);

		if(this.getBlockEntity() != null) {
			this.addSlotBox(SLOTGROUP_RACKSLOTS, this.getBlockEntity().rackInventory, 0, 20, 20, 2, 20, 2, 20);
		}

		this.allowSlotGroupMovement(SLOTGROUP_PLAYER, SLOTGROUP_RACKSLOTS, true);
	}

}
