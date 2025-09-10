package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.setup.DeferredRegistries;
import com.davenonymous.smarthome.setup.blocks.HACobbleItem;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {

	public static final DeferredItem<BlockItem> HA_COBBLE_ITEM = DeferredRegistries.ITEMS.register("ha_cobble", HACobbleItem::new);
}
