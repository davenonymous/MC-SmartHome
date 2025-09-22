package com.davenonymous.smarthome.datagen;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.minirack.MiniRackBlock;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.content.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
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

		miniRackBlock();

		itemModels().basicItem(ModItems.IRDA_ITEM.get());
		itemModels().basicItem(ModItems.SERVER_ITEM.get());
		itemModels().basicItem(ModItems.RANGE_FINDER_ITEM.get());
		itemModels().basicItem(ModItems.PROJECT_BOX_ITEM.get());
	}

	public void simpleBlockWithItem(Block block) {
		var model = cubeAll(block);
		this.simpleBlockWithItem(block, model);
	}

	public void miniRackBlock() {
		var block = ModBlocks.MINI_RACK.get();
		var miniRackModel = new ModelFile.ExistingModelFile(SmartHome.resource("block/mini_rack"), this.exFileHelper);
		var diskA = new ModelFile.ExistingModelFile(SmartHome.resource("block/mini_rack_disk_a"), this.exFileHelper);
		var diskB = new ModelFile.ExistingModelFile(SmartHome.resource("block/mini_rack_disk_b"), this.exFileHelper);
		var diskC = new ModelFile.ExistingModelFile(SmartHome.resource("block/mini_rack_disk_c"), this.exFileHelper);
		var diskD = new ModelFile.ExistingModelFile(SmartHome.resource("block/mini_rack_disk_d"), this.exFileHelper);

		simpleBlockItem(ModBlocks.MINI_RACK.get(), miniRackModel);

		MultiPartBlockStateBuilder builder = this.getMultipartBuilder(block);
		for(var direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
			for(var face : BlockStateProperties.ATTACH_FACE.getPossibleValues()) {
				var minirackBuilder = builder.part().modelFile(miniRackModel);
				if(face == AttachFace.FLOOR) {
					minirackBuilder = minirackBuilder.rotationX(270);
				} else if(face == AttachFace.CEILING) {
					minirackBuilder = minirackBuilder.rotationX(90);
				}

				minirackBuilder = minirackBuilder.rotationY(((int)direction.toYRot() + 180) % 360);
				minirackBuilder.addModel()
					.condition(BlockStateProperties.HORIZONTAL_FACING, direction)
					.condition(BlockStateProperties.ATTACH_FACE, face);

				createDiskPart(direction, face, builder, diskA, MiniRackBlock.SLOT1);
				createDiskPart(direction, face, builder, diskB, MiniRackBlock.SLOT2);
				createDiskPart(direction, face, builder, diskC, MiniRackBlock.SLOT3);
				createDiskPart(direction, face, builder, diskD, MiniRackBlock.SLOT4);
			}
		}
	}

	private static void createDiskPart(Direction direction, AttachFace face, MultiPartBlockStateBuilder builder, ModelFile.ExistingModelFile diskA, BooleanProperty slot) {
		var diskABuilder = builder.part().modelFile(diskA);
		if(face == AttachFace.FLOOR) {
			diskABuilder = diskABuilder.rotationX(270);
		} else if(face == AttachFace.CEILING) {
			diskABuilder = diskABuilder.rotationX(90);
		}
		diskABuilder = diskABuilder.rotationY(((int) direction.toYRot() + 180) % 360);
		diskABuilder.addModel()
			.condition(BlockStateProperties.HORIZONTAL_FACING, direction)
			.condition(BlockStateProperties.ATTACH_FACE, face)
			.condition(slot, true);
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
