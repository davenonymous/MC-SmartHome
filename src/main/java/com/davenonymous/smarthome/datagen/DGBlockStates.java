package com.davenonymous.smarthome.datagen;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class DGBlockStates extends BlockStateProvider {
	private final ExistingFileHelper exFileHelper;

	public DGBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, SmartHome.MODID, exFileHelper);
		this.exFileHelper = exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		var wallMountedDashboardModel = new ModelFile.ExistingModelFile(SmartHome.resource("block/wall_dashboard"), this.exFileHelper);
		ownDirectionalBlock(ModBlocks.WALL_DASHBOARD.get(), wallMountedDashboardModel);
		simpleBlockItem(ModBlocks.WALL_DASHBOARD.get(), wallMountedDashboardModel);
	}

	public void ownDirectionalBlock(Block block, ModelFile model) {
		this.getVariantBuilder(block).forAllStates((state) -> {
			Direction dir = state.getValue(BlockStateProperties.FACING);
			var builder = ConfiguredModel.builder().modelFile(model);
			if(dir == Direction.DOWN) {
				builder = builder.rotationX(90);
			} else if(dir == Direction.UP) {
				builder = builder.rotationX(270);
			} else {
				builder = builder.rotationY(((int)dir.toYRot() + 180) % 360);
			}
			return builder.build();
		});
	}
}
