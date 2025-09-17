package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.WallDashboardBlock;
import com.davenonymous.smarthome.blocks.WallDashboardBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SmartHome.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SmartHome.MODID);
	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, SmartHome.MODID);


	public static final DeferredBlock<Block> WALL_DASHBOARD = BLOCKS.register(
		"wall_dashboard",
		() -> new WallDashboardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

	public static final Supplier<BlockEntityType<WallDashboardBlockEntity>> DASHBOARD_ENTITY = BLOCK_ENTITIES.register(
		"wall_dashboard",
		() -> BlockEntityType.Builder.of(WallDashboardBlockEntity::new, WALL_DASHBOARD.get())
			.build(null)
	);

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WallDashboardBlock>> DASHBOARD_BLOCK_TYPE = BLOCK_TYPES.register(
		"wall_dashboard",
		() -> BlockBehaviour.simpleCodec(WallDashboardBlock::new)
	);
}
