package com.davenonymous.smarthome.blocks;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
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

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<AttachFace> ATTACH_FACE = BlockStateProperties.ATTACH_FACE;

	public WallDashboardBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(HORIZONTAL_FACING, Direction.NORTH)
				.setValue(ATTACH_FACE, AttachFace.WALL));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if(!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.PASS;
		}

		WorldSavedHomes data = WorldSavedHomes.get((ServerLevel) level);
		var optZone = data.getHome(pos);
		if(optZone.isPresent()) {
			PacketDistributor.sendToPlayer(serverPlayer, new HomeInfoPayload(optZone.get().home()));
			return InteractionResult.CONSUME;
		}

		var dummy = new HomeCore("dummy");
		PacketDistributor.sendToPlayer(serverPlayer, new HomeInfoPayload(dummy));
		return InteractionResult.CONSUME;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HORIZONTAL_FACING);
		builder.add(ATTACH_FACE);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
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

		return this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, forward)
			.setValue(ATTACH_FACE, attachFace);
	}

	private VoxelShape getShape(BlockState state) {
		var direction = state.getValue(HORIZONTAL_FACING);
		if(state.getValue(ATTACH_FACE) == AttachFace.CEILING) {
			direction = Direction.DOWN;
		} else if(state.getValue(ATTACH_FACE) == AttachFace.FLOOR) {
			direction = Direction.UP;
		}
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
