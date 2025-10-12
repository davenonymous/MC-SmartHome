package com.davenonymous.smarthome.setup.datagen;


import com.davenonymous.smarthome.setup.content.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class DGRecipes extends RecipeProvider {
	public DGRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WALL_DASHBOARD_ITEM.get())
			.pattern("   ")
			.pattern("b b")
			.pattern("bbb")
			.define('b', Tags.Items.BRICKS)
			.unlockedBy("has_bricks", has(Tags.Items.BRICKS))
			.save(recipeOutput);

	}
}
