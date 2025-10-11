package com.davenonymous.smarthome.blocks.dashboard;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.lib.gui.WidgetBlockEntityContainer;
import com.davenonymous.smarthome.networking.OpenHomeScreenPayload;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.content.ModContainers;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DashboardContainer extends WidgetBlockEntityContainer<DashboardBlockEntity> {

	public List<HomeCore> ownedHomes;
	public HomeWorldInfo homeWorldInfo;

	public DashboardContainer(int id, BlockPos pos, Inventory inv, @NotNull Player player) {
		super(ModContainers.DASHBOARD_CONTAINER.get(), id, pos, inv, player);
	}

	public DashboardContainer(int id, Inventory playerInv, RegistryFriendlyByteBuf buf) {
		super(ModContainers.DASHBOARD_CONTAINER.get(), id, buf.readBlockPos(), playerInv, playerInv.player);

		var data = OpenHomeScreenPayload.CODEC.decode(buf);
		ownedHomes = data.homes();
		homeWorldInfo = data.worldInfo();

		for(var home : ownedHomes) {
			WorldWatcherUtil.autoIgnoreGenericOnlyDevices(home);
		}
	}



}
