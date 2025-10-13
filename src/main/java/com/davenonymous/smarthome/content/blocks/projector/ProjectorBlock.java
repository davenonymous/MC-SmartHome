package com.davenonymous.smarthome.content.blocks.projector;

import com.davenonymous.smarthome.content.blocks.base.FacingBaseBlock;
import com.davenonymous.smarthome.content.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.content.blocks.dashboard.DashboardContainer;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.OpenHomeScreenPayload;
import com.davenonymous.smarthome.networking.OpenProjectorScreenPayload;
import com.google.common.collect.Table;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ProjectorBlock extends FacingBaseBlock implements EntityBlock {
	private static Table<AttachFace, Direction, VoxelShape> SHAPES = calculateShapes(Shapes.box(0, 0, 0, 1, 1/8f, 1/8f));

	public ProjectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
		return (level1, blockPos, blockState, blockEntity) -> {
			ProjectorBlockEntity projector = (ProjectorBlockEntity) level1.getBlockEntity(blockPos);
			if(projector == null) {
				return;
			}

			if(level1.isClientSide()) {
				projector.clientTick(level1, blockPos, blockState);
			} else {
				projector.serverTick((ServerLevel) level1, blockPos, blockState);
			}
		};
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		ProjectorBlockEntity entity = (ProjectorBlockEntity) level.getBlockEntity(pos);
		if(entity != null && placer != null) {
			entity.setOwnerUUID(placer.getUUID());
			entity.setChanged();
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if(!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.PASS;
		}

		ProjectorBlockEntity entity = (ProjectorBlockEntity) level.getBlockEntity(pos);
		if(entity == null) {
			return InteractionResult.PASS;
		}

		WorldSavedHomes data = WorldSavedHomes.get((ServerLevel) level);
		UUID homeId = entity.home();
		if(homeId == null) {
			var homes = data.getPlayerHomes(entity.ownerUUID());
			if(!homes.isEmpty()) {
				homeId = homes.getFirst().id();
				entity.setHome(homeId);
				entity.setChanged();
			}
		}
		if(homeId == null) {
			homeId = HomeBlockEntity.emptyUUID;
		}

		UUID cardId = entity.selectedCard();
		var homes = data.getPlayerHomes(player.getUUID());
		if(cardId == null) {
			cardId = HomeBlockEntity.emptyUUID;
		}

		var extraData = new OpenProjectorScreenPayload(homes, homeId, cardId);
		player.openMenu(this.getMenuProvider(state, level, pos), registryFriendlyByteBuf -> {
			registryFriendlyByteBuf.writeBlockPos(pos);
			OpenProjectorScreenPayload.CODEC.encode(registryFriendlyByteBuf, extraData);
		});
		return InteractionResult.SUCCESS_NO_ITEM_USED;
	}

	@Override
	public VoxelShape getShape(Direction facing, AttachFace attachFace) {
		return SHAPES.get(attachFace, facing);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.block();
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new ProjectorBlockEntity(blockPos, blockState);
	}

	@Override
	protected @NotNull MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider(
			(id, inventory, player) -> new ProjectorContainer(id, pos, inventory, player),
			Component.translatable("block.smarthome.projector")
		);
	}
}
