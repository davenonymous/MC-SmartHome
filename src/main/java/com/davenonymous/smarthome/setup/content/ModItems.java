package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.minirack.MiniRackitem;
import com.davenonymous.smarthome.blocks.dashboard.DashboardItem;
import com.davenonymous.smarthome.items.IrdaTransceiverItem;
import com.davenonymous.smarthome.items.projectbox.ProjectBoxItem;
import com.davenonymous.smarthome.items.RangeFinderItem;
import com.davenonymous.smarthome.items.ServerItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmartHome.MODID);
	public static final DeferredItem<BlockItem> WALL_DASHBOARD_ITEM = ITEMS.register(ModBlocks.DASHBOARD_ID, DashboardItem::new);
	public static final DeferredItem<BlockItem> MINI_RACK_ITEM = ITEMS.register(ModBlocks.MINI_RACK_ID, MiniRackitem::new);

	public static final DeferredItem<ServerItem> SERVER_ITEM = ITEMS.register("server", ServerItem::new);
	public static final DeferredItem<IrdaTransceiverItem> IRDA_ITEM = ITEMS.register("irda_transceiver", IrdaTransceiverItem::new);
	public static final DeferredItem<RangeFinderItem> RANGE_FINDER_ITEM = ITEMS.register("range_finder", RangeFinderItem::new);
	public static final DeferredItem<ProjectBoxItem> PROJECT_BOX_ITEM = ITEMS.register("project_box", ProjectBoxItem::new);
}
