package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.setup.DeferredRegistries;
import com.davenonymous.smarthome.setup.blocks.HACobbleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks {

	public static final DeferredBlock<Block> HA_COBBLE = DeferredRegistries.BLOCKS.register("ha_cobble", () -> new HACobbleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
}
