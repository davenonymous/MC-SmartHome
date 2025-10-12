package com.davenonymous.smarthome.setup.datagen;


import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class DGBlockTags extends BlockTagsProvider {
	public DGBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, SmartHome.MODID, existingFileHelper);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE).add(
			ModBlocks.DASHBOARD.get(), ModBlocks.MINI_RACK.get(), ModBlocks.PROJECTOR.get()
		);
	}
}
