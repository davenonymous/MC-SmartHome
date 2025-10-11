package com.davenonymous.smarthome.blocks.dashboard;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class DashboardBlockEntity extends HomeBlockEntity {
	private ResourceLocation selectedTab;

	public DashboardBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.DASHBOARD_ENTITY.get(), pos, blockState);
		selectedTab = SmartHome.resource("zones");
	}

	public ResourceLocation selectedTab() {
		return selectedTab;
	}

	public DashboardBlockEntity setSelectedTab(ResourceLocation selectedTab) {
		if(this.selectedTab != null && this.selectedTab.equals(selectedTab)) {
			return this;
		}

		this.selectedTab = selectedTab;
		this.setChanged();
		return this;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if(tag.contains("selectedTab")) {
			selectedTab = ResourceLocation.parse(tag.getString("selectedTab"));
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if(selectedTab != null) {
			tag.putString("selectedTab", selectedTab.toString());
		}
	}
}
