package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.MiniRackBlock;
import com.davenonymous.smarthome.blocks.MiniRackBlockEntity;
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


	public static final String WALL_DASHBOARD_ID = "wall_dashboard";
	public static final DeferredBlock<Block> WALL_DASHBOARD = BLOCKS.register(
		WALL_DASHBOARD_ID, () -> new WallDashboardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

	public static final Supplier<BlockEntityType<WallDashboardBlockEntity>> DASHBOARD_ENTITY = BLOCK_ENTITIES.register(
		WALL_DASHBOARD_ID, () -> BlockEntityType.Builder.of(WallDashboardBlockEntity::new, WALL_DASHBOARD.get()).build(null));

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WallDashboardBlock>> DASHBOARD_BLOCK_TYPE = BLOCK_TYPES.register(
		WALL_DASHBOARD_ID, () -> BlockBehaviour.simpleCodec(WallDashboardBlock::new));


	public static final String MINI_RACK_ID = "mini_rack";
	public static final DeferredBlock<Block> MINI_RACK = BLOCKS.register(
		MINI_RACK_ID, () -> new MiniRackBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

	public static final Supplier<BlockEntityType<MiniRackBlockEntity>> MINI_RACK_ENTITY = BLOCK_ENTITIES.register(
		MINI_RACK_ID, () -> BlockEntityType.Builder.of(MiniRackBlockEntity::new, MINI_RACK.get()).build(null));

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<MiniRackBlock>> MINI_RACK_BLOCK_TYPE = BLOCK_TYPES.register(
		MINI_RACK_ID, () -> BlockBehaviour.simpleCodec(MiniRackBlock::new));
}
