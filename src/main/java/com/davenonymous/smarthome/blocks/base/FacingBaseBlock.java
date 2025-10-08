package com.davenonymous.smarthome.blocks.base;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

	protected static VoxelShape shapeFromAABBs(List<AABB> aabbs) {
		var result = Shapes.create(aabbs.getFirst());
		for(int i = 1; i < aabbs.size(); i++) {
			result = Shapes.joinUnoptimized(result, Shapes.create(aabbs.get(i)), BooleanOp.OR);
		}
		return result;
	}

	protected static VoxelShape rotateShape(VoxelShape shape, Direction from, Direction to) {
		if(from == to) {
			return shape;
		}
		var aabbs = shape.toAabbs();

		if(from == Direction.NORTH && to == Direction.DOWN) {
			aabbs = aabbs.stream().map(bb -> new AABB(
				bb.minX, 1 - bb.maxZ, bb.minY,
				bb.maxX, 1 - bb.minZ, bb.maxY
			)).toList();
			return shapeFromAABBs(aabbs);
		}

		if(from == Direction.NORTH && to == Direction.UP) {
			aabbs = aabbs.stream().map(bb -> new AABB(
				bb.minX, bb.minZ, 1 - bb.maxY,
				bb.maxX, bb.maxZ, 1 - bb.minY
			)).toList();
			return shapeFromAABBs(aabbs);
		}

		while(from != to) {
			aabbs = aabbs.stream().map(bb -> new AABB(
				1 - bb.minZ, bb.minY, bb.minX,
				1 - bb.maxZ, bb.maxY, bb.maxX
			)).toList();
			from = from.getClockWise();
		}

		return shapeFromAABBs(aabbs);
	}

	protected static Table<AttachFace, Direction, VoxelShape> calculateShapes(VoxelShape northFacingShape) {
		Table<AttachFace, Direction, VoxelShape> table = HashBasedTable.create();
		table.put(AttachFace.WALL, Direction.NORTH, northFacingShape);
		table.put(AttachFace.WALL, Direction.EAST, rotateShape(northFacingShape, Direction.NORTH, Direction.EAST));
		table.put(AttachFace.WALL, Direction.SOUTH, rotateShape(northFacingShape, Direction.NORTH, Direction.SOUTH));
		table.put(AttachFace.WALL, Direction.WEST, rotateShape(northFacingShape, Direction.NORTH, Direction.WEST));

		var upShape = rotateShape(northFacingShape, Direction.NORTH, Direction.UP);
		table.put(AttachFace.CEILING, Direction.NORTH, upShape);
		table.put(AttachFace.CEILING, Direction.EAST, rotateShape(upShape, Direction.NORTH, Direction.EAST));
		table.put(AttachFace.CEILING, Direction.SOUTH, rotateShape(upShape, Direction.NORTH, Direction.SOUTH));
		table.put(AttachFace.CEILING, Direction.WEST, rotateShape(upShape, Direction.NORTH, Direction.WEST));

		var downShape = rotateShape(northFacingShape, Direction.NORTH, Direction.DOWN);
		table.put(AttachFace.FLOOR, Direction.NORTH, downShape);
		table.put(AttachFace.FLOOR, Direction.EAST, rotateShape(downShape, Direction.NORTH, Direction.EAST));
		table.put(AttachFace.FLOOR, Direction.SOUTH, rotateShape(downShape, Direction.NORTH, Direction.SOUTH));
		table.put(AttachFace.FLOOR, Direction.WEST, rotateShape(downShape, Direction.NORTH, Direction.WEST));

		return table;
	}
}
