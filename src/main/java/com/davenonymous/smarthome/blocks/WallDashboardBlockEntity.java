package com.davenonymous.smarthome.blocks;

import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class WallDashboardBlockEntity extends HomeBlockEntity {

	public WallDashboardBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.DASHBOARD_ENTITY.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
	}
}
