package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.blocks.minirack.MiniRackBlock;
import com.davenonymous.smarthome.content.blocks.minirack.MiniRackBlockEntity;
import com.davenonymous.smarthome.content.blocks.dashboard.DashboardBlock;
import com.davenonymous.smarthome.content.blocks.dashboard.DashboardBlockEntity;
import com.davenonymous.smarthome.content.blocks.projector.ProjectorBlock;
import com.davenonymous.smarthome.content.blocks.projector.ProjectorBlockEntity;
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


	public static final String DASHBOARD_ID = "dashboard";
	public static final DeferredBlock<Block> DASHBOARD = BLOCKS.register(
		DASHBOARD_ID, () -> new DashboardBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

	public static final Supplier<BlockEntityType<DashboardBlockEntity>> DASHBOARD_ENTITY = BLOCK_ENTITIES.register(
		DASHBOARD_ID, () -> BlockEntityType.Builder.of(DashboardBlockEntity::new, DASHBOARD.get()).build(null));

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<DashboardBlock>> DASHBOARD_BLOCK_TYPE = BLOCK_TYPES.register(
		DASHBOARD_ID, () -> BlockBehaviour.simpleCodec(DashboardBlock::new));


	public static final String PROJECTOR_ID = "projector";
	public static final DeferredBlock<Block> PROJECTOR = BLOCKS.register(
		PROJECTOR_ID, () -> new ProjectorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

	public static final Supplier<BlockEntityType<ProjectorBlockEntity>> PROJECTOR_ENTITY = BLOCK_ENTITIES.register(
		PROJECTOR_ID, () -> BlockEntityType.Builder.of(ProjectorBlockEntity::new, PROJECTOR.get()).build(null));

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ProjectorBlock>> PROJECTOR_BLOCK_TYPE = BLOCK_TYPES.register(
		PROJECTOR_ID, () -> BlockBehaviour.simpleCodec(ProjectorBlock::new));



	public static final String MINI_RACK_ID = "mini_rack";
	public static final DeferredBlock<Block> MINI_RACK = BLOCKS.register(
		MINI_RACK_ID, () -> new MiniRackBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

	public static final Supplier<BlockEntityType<MiniRackBlockEntity>> MINI_RACK_ENTITY = BLOCK_ENTITIES.register(
		MINI_RACK_ID, () -> BlockEntityType.Builder.of(MiniRackBlockEntity::new, MINI_RACK.get()).build(null));

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<MiniRackBlock>> MINI_RACK_BLOCK_TYPE = BLOCK_TYPES.register(
		MINI_RACK_ID, () -> BlockBehaviour.simpleCodec(MiniRackBlock::new));
}
