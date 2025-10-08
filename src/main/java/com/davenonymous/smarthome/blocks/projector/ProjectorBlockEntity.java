package com.davenonymous.smarthome.blocks.projector;

import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class ProjectorBlockEntity extends HomeBlockEntity {
	private UUID selectedCard;

	public ProjectorBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.PROJECTOR_ENTITY.get(), pos, blockState);

	}

	public UUID selectedCard() {
		return selectedCard;
	}

	public ProjectorBlockEntity setSelectedCard(UUID selectedCard) {
		if(this.selectedCard != null && this.selectedCard.equals(selectedCard)) {
			return this;
		}

		this.selectedCard = selectedCard;
		this.setChanged();
		return this;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if(tag.contains("card")) {
			selectedCard = tag.getUUID("card");
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if(selectedCard != null) {
			tag.putUUID("card", selectedCard);
		}
	}
}
