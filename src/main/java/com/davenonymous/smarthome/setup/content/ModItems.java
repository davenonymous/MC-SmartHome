package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.WallDashboardItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmartHome.MODID);
	public static final DeferredItem<BlockItem> WALL_DASHBOARD_ITEM = ITEMS.register("wall_dashboard", WallDashboardItem::new);
}
