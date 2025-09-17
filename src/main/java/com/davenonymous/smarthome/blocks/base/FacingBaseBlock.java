package com.davenonymous.smarthome.blocks.base;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class FacingBaseBlock extends BaseBlock {

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;

	public FacingBaseBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(HORIZONTAL_FACING, Direction.NORTH)
				.setValue(ATTACH_FACE, AttachFace.WALL));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HORIZONTAL_FACING);
		builder.add(ATTACH_FACE);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
		var state = super.getStateForPlacement(context);
		if(state == null) {
			state = this.defaultBlockState();
		}
		var forward = context.getHorizontalDirection();
		var player = context.getPlayer();
		var attachFace = AttachFace.WALL;

		if (player != null) {
			if (player.getXRot() > 60) {
				attachFace = AttachFace.CEILING;
			} else if (player.getXRot() < -60) {
				attachFace = AttachFace.FLOOR;
			}
		}

		return state
			.setValue(HORIZONTAL_FACING, forward)
			.setValue(ATTACH_FACE, attachFace);
	}

	@Override
	public VoxelShape getShape(BlockState state) {
		var direction = state.getValue(HORIZONTAL_FACING);
		var attachFace = state.getValue(ATTACH_FACE);
		return getShape(direction, attachFace);
	}

	public abstract VoxelShape getShape(Direction facing, AttachFace attachFace);
}
