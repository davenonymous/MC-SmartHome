package com.davenonymous.smarthome.datagen;

import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class DGBlockLoot extends BlockLootSubProvider {
	protected DGBlockLoot(HolderLookup.Provider registries) {
		super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return ModBlocks.BLOCKS.getEntries().stream()
			.map(entry -> (Block) entry.value())
			.toList();
	}

	@Override
	protected void generate() {
		createCopyComponentsBlockEntityTable(ModBlocks.DASHBOARD.get());
		createCopyComponentsBlockEntityTable(ModBlocks.MINI_RACK.get());
	}

	protected void createCopyComponentsBlockEntityTable(Block block) {
		var table = LootTable.lootTable()
			.withPool(this.applyExplosionCondition(
					block,
					LootPool.lootPool()
						.setRolls(ConstantValue.exactly(1.0F))
						.add(LootItem.lootTableItem(block)
							.apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
								.include(DataComponents.CUSTOM_NAME)
							)
						)
				)
			);
		this.add(block, table);
	}
}
