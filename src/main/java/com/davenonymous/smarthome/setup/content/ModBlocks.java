package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.WallDashboardBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SmartHome.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SmartHome.MODID);
	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, SmartHome.MODID);


	public static final DeferredBlock<Block> WALL_DASHBOARD = BLOCKS.register("wall_dashboard", () -> new WallDashboardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));
}
