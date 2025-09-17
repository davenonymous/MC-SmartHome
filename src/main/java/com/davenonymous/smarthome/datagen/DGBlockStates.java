package com.davenonymous.smarthome.datagen;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
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
		var dashboardModel = new ModelFile.ExistingModelFile(SmartHome.resource("block/dashboard"), this.exFileHelper);
		ownDirectionalBlock(ModBlocks.DASHBOARD.get(), dashboardModel);
		simpleBlockItem(ModBlocks.DASHBOARD.get(), dashboardModel);

		simpleBlockWithItem(ModBlocks.MINI_RACK.get());
	}

	public void simpleBlockWithItem(Block block) {
		var model = cubeAll(block);
		this.simpleBlockWithItem(block, model);
	}

	public void ownDirectionalBlock(Block block, ModelFile model) {
		this.getVariantBuilder(block).forAllStates((state) -> {
			Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);

			var builder = ConfiguredModel.builder().modelFile(model);
			if(face == AttachFace.FLOOR) {
				builder = builder.rotationX(270);
			} else if(face == AttachFace.CEILING) {
				builder = builder.rotationX(90);
			}

			builder = builder.rotationY(((int)dir.toYRot() + 180) % 360);
			return builder.build();
		});
	}
}
