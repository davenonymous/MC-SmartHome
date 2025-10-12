package com.davenonymous.smarthome.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.function.Function;

public class WorldPlacerUtil {

	public enum Material {
		WALL,
		BRICK,
		GLASS,
		ROOF,
		STAIRS,
		DOOR,
		TORCH,
		FLOOR
	}

	public static Function<Material, BlockState> defaultMaterialGetter = (mat) -> switch(mat) {
		case WALL -> Blocks.STONE_BRICKS.defaultBlockState();
		case BRICK -> Blocks.BRICKS.defaultBlockState();
		case GLASS -> Blocks.GLASS.defaultBlockState();
		case ROOF -> Blocks.DARK_OAK_SLAB.defaultBlockState();
		case STAIRS -> Blocks.DARK_OAK_STAIRS.defaultBlockState();
		case DOOR -> Blocks.OAK_DOOR.defaultBlockState();
		case TORCH -> Blocks.TORCH.defaultBlockState();
		case FLOOR -> Blocks.OAK_PLANKS.defaultBlockState();
	};

	public static void build9by9(BlockPos startPos, ServerLevel world, Function<Material, BlockState> matsGetter) {

		for(int x = 0; x < 9; x++) {
			for(int z = 0; z < 9; z++) {
				for(int y = 0; y < 6; y++) {
					BlockPos pos = startPos.offset(x, y, z);
					if(x == 0 || x == 8 || z == 0 || z == 8) {
						if(y == 1 && (x == 4 || z == 4)) {
							world.setBlock(pos, matsGetter.apply(Material.DOOR).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), Block.UPDATE_ALL);
						} else if(y == 4 && (x == 2 || x == 6) && (z == 0 || z == 8)) {
							world.setBlock(pos, matsGetter.apply(Material.GLASS), Block.UPDATE_ALL);
						} else if(y == 4 && (z == 2 || z == 6) && (x == 0 || x == 8)) {
							world.setBlock(pos, matsGetter.apply(Material.GLASS), Block.UPDATE_ALL);
						} else if(y == 2 && (x == 4 || z == 4)) {
							world.setBlock(pos, matsGetter.apply(Material.DOOR).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
						} else {
							world.setBlock(pos, matsGetter.apply(Material.WALL), Block.UPDATE_ALL);
						}
					} else if(y == 5) {
						if((x == 2 || x == 3 || x == 5 || x == 6) && (z == 2 || z == 3 || z == 5 || z == 6)) {
							world.setBlock(pos, matsGetter.apply(Material.GLASS), Block.UPDATE_ALL);
						} else {
							world.setBlock(pos, matsGetter.apply(Material.ROOF), Block.UPDATE_ALL);
						}
					} else if(y == 0) {
						world.setBlock(pos, matsGetter.apply(Material.BRICK), Block.UPDATE_ALL);
					} else {
						world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
					}
				}
			}
		}

		world.setBlock(startPos.offset(1,1,1), matsGetter.apply(Material.TORCH), Block.UPDATE_ALL);
		world.setBlock(startPos.offset(7,1,1), matsGetter.apply(Material.TORCH), Block.UPDATE_ALL);
		world.setBlock(startPos.offset(1,1,7), matsGetter.apply(Material.TORCH), Block.UPDATE_ALL);
		world.setBlock(startPos.offset(7,1,7), matsGetter.apply(Material.TORCH), Block.UPDATE_ALL);
	}
}
