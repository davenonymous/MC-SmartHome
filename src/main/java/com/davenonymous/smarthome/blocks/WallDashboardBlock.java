package com.davenonymous.smarthome.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class WallDashboardBlock extends Block {
	private static Map<Direction, VoxelShape> SHAPES = Map.of(
		Direction.NORTH, Shapes.box(0.1875, 0.0625, 0, 0.8125, 0.6875, 0.3125),
		Direction.SOUTH, Shapes.box(0.1875, 0.0625, 1-0.3125, 0.8125, 0.6875, 1),
		Direction.EAST,  Shapes.box(1-0.3125, 0.0625, 0.1875, 1, 0.6875, 0.8125),
		Direction.WEST,  Shapes.box(0, 0.0625, 0.1875, 0.3125, 0.6875, 0.8125),
		Direction.UP,    Shapes.box(0.1875, 1-0.3125, 0.1875, 0.8125, 1, 0.8125),
		Direction.DOWN,  Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.3125, 0.8125)
	);

	public WallDashboardBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(BlockStateProperties.FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		var forward = context.getHorizontalDirection();
		var player = context.getPlayer();
		if (player != null) {
			if (player.getXRot() > 65) {
				forward = Direction.UP;
			} else if (player.getXRot() < -65) {
				forward = Direction.DOWN;
			}
		}

		return this.defaultBlockState().setValue(BlockStateProperties.FACING, forward);
	}

	private VoxelShape getShape(BlockState state) {
		var direction = state.getValue(BlockStateProperties.FACING);
		return SHAPES.get(direction);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShape(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShape(state);
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return getShape(state);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShape(state);
	}
}
