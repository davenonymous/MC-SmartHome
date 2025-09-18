package com.davenonymous.smarthome.datagen;


import com.davenonymous.smarthome.SmartHome;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SmartHome.MODID)
public class DGHandler {
	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		var blockTagsProvider = new DGBlockTags(output, lookupProvider, existingFileHelper);
		generator.addProvider(event.includeClient(), blockTagsProvider);
		generator.addProvider(event.includeServer(), new DGBlockStates(output, existingFileHelper));
		generator.addProvider(event.includeServer(), new DGRecipes(output, lookupProvider));
		generator.addProvider(event.includeClient(), new DGTranslations(output, SmartHome.MODID, "en_us"));
		generator.addProvider(event.includeClient(), new DGFonts(SmartHome.MODID, output, existingFileHelper));

		// Block-Loot
		List<LootTableProvider.SubProviderEntry> lootTableSources = new ArrayList<>();
		lootTableSources.add(new LootTableProvider.SubProviderEntry(DGBlockLoot::new, LootContextParamSets.BLOCK));
		generator.addProvider(event.includeServer(), new LootTableProvider(output, Collections.emptySet(), lootTableSources, lookupProvider));
	}
}
