package com.davenonymous.smarthome.blocks;

import com.davenonymous.smarthome.blocks.base.FacingBaseBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MiniRackBlock extends FacingBaseBlock implements EntityBlock {
	public static final BooleanProperty SLOT1 = BooleanProperty.create("slot1");
	public static final BooleanProperty SLOT2 = BooleanProperty.create("slot2");
	public static final BooleanProperty SLOT3 = BooleanProperty.create("slot3");
	public static final BooleanProperty SLOT4 = BooleanProperty.create("slot4");
	public static final BooleanProperty[] SLOTS = new BooleanProperty[]{SLOT1, SLOT2, SLOT3, SLOT4};

	public static final VoxelShape SHAPE = Shapes.block();

	public MiniRackBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SLOT1);
		builder.add(SLOT2);
		builder.add(SLOT3);
		builder.add(SLOT4);
	}

	@Override
	public VoxelShape getShape(Direction facing, AttachFace attachFace) {
		return SHAPE;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new MiniRackBlockEntity(blockPos, blockState);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		MiniRackBlockEntity entity = (MiniRackBlockEntity) level.getBlockEntity(pos);
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

		if(level.getBlockEntity(pos) instanceof MiniRackBlockEntity miniRack) {
			player.openMenu(state.getMenuProvider(level, pos), pos);
		}

		return super.useWithoutItem(state, level, pos, player, hitResult);
	}

	@Override
	protected @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		return new SimpleMenuProvider(
			(id, inventory, player) -> new MiniRackContainer(id, pos, inventory, player),
			Component.translatable("block.smarthome.mini_rack")
		);
	}
}
