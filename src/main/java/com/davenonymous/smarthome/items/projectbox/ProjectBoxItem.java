package com.davenonymous.smarthome.items.projectbox;

import com.davenonymous.smarthome.data.PlacedProjectBox;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.entities.PlacedProjectBoxEntity;
import com.davenonymous.smarthome.items.BaseItem;
import com.davenonymous.smarthome.items.IWorldRenderer;
import com.davenonymous.smarthome.items.RangeFinderItem;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.davenonymous.smarthome.setup.content.ModParticleModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;

public class ProjectBoxItem extends BaseItem implements IWorldRenderer {


	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		if(level.isClientSide() || hand == InteractionHand.OFF_HAND) {
			// Client-side only, no need to check server-side conditions
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		}

		var stack = player.getItemInHand(hand);
		if(!stack.has(ModDataComponents.PROJECT_BOX_DATA_COMPONENT)) {
			var newComponent = new ProjectBoxDataComponent();
			stack.set(ModDataComponents.PROJECT_BOX_DATA_COMPONENT, newComponent);
			openGUI(player);
			return InteractionResultHolder.success(player.getItemInHand(hand));
		}

		if(player.isSecondaryUseActive()) {
			openGUI(player);
			return InteractionResultHolder.success(player.getItemInHand(hand));
		}

		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	private void openGUI(Player player) {
		var provider = new SimpleMenuProvider((id, inventory, pPlayer) -> new ProjectBoxContainer(id, pPlayer.getInventory(), pPlayer), Component.empty());
		player.openMenu(provider);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		var level = context.getLevel();
		var player = context.getPlayer();

		if(player == null || !player.mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {
			return InteractionResult.PASS;
		}

		if(level.isClientSide()) {
			// Client-side only, no need to check server-side conditions
			return InteractionResult.PASS;
		}

		var boxData = stack.get(ModDataComponents.PROJECT_BOX_DATA_COMPONENT);
		if(boxData == null) {
			player.sendSystemMessage(Component.translatable("smarthome.message.project_box.empty"));
			return InteractionResult.PASS;
		}

		var errorMessage = boxData.placementErrorMessage();
		if(errorMessage != null) {
			player.sendSystemMessage(errorMessage);
			return InteractionResult.PASS;
		}

		// When the player clicked on the outside of a block, we need to offset the position
		// to the adjacent block in the direction of the clicked face
		var clickedPosInZone = context.getClickedPos();
		if(!context.isInside()) {
			clickedPosInZone = clickedPosInZone.relative(context.getClickedFace());
		}

		var data = WorldSavedHomes.get((ServerLevel) level);
		var optZone = data.getPlayerHome(clickedPosInZone);
		if(optZone.isEmpty()) {
			player.sendSystemMessage(Component.translatable("smarthome.message.project_box.outside_home"));
			return InteractionResult.PASS;
		}

		var zone = optZone.get();
		var location = context.getClickLocation();
		var face = context.getClickedFace();
		var pos = location.relative(face, 1/8f);

		var box = new PlacedProjectBox(context.getClickedPos(), pos, face, boxData);
		//zone.addProjectBox(box);
		//data.setDirty();

		var entity = new PlacedProjectBoxEntity(level, pos, face);
		level.addFreshEntity(entity);

		player.sendSystemMessage(Component.translatable("smarthome.message.project_box.placed", boxData.name(), zone.name()));

		return InteractionResult.CONSUME;
	}

	@Override
	public RenderLevelStageEvent.Stage renderStage() {
		return RenderLevelStageEvent.Stage.AFTER_PARTICLES;
	}

	@Override
	public void renderWorld(RenderLevelStageEvent event, ItemStack heldItem) {
		var level = Minecraft.getInstance().level;
		var player = Minecraft.getInstance().player;
		BlockHitResult blockHit = RangeFinderItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY, (float)player.blockInteractionRange());
		if(blockHit.getType() != BlockHitResult.Type.BLOCK) {
			return;
		}

		var exactHitLocation = blockHit.getLocation();
		var hitFace = blockHit.getDirection();

		var renderType = RenderType.CUTOUT;
		var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		var consumer = bufferSource.getBuffer(renderType);

		var pose = event.getPoseStack();
		pose.pushPose();
		var camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		pose.translate(-camPos.x, -camPos.y, -camPos.z);
		pose.translate(exactHitLocation.x, exactHitLocation.y, exactHitLocation.z);

		Quaternionf quatty = new Quaternionf();
		if(hitFace == Direction.NORTH) {
			quatty.mul(new Quaternionf().fromAxisAngleDeg(0, 1, 0, 180));
		} else if(hitFace == Direction.UP) {
			quatty.mul(new Quaternionf().fromAxisAngleDeg(1, 0, 0, -90));
		} else if(hitFace == Direction.DOWN) {
			quatty.mul(new Quaternionf().fromAxisAngleDeg(1, 0, 0, 90));
		} else if(hitFace == Direction.WEST) {
			quatty.mul(new Quaternionf().fromAxisAngleDeg(0, 1, 0, -90));
		} else if(hitFace == Direction.EAST) {
			quatty.mul(new Quaternionf().fromAxisAngleDeg(0, 1, 0, 90));
		}
		pose.mulPose(quatty);

		var centerOffset = ModParticleModels.PROJECT_BOX_AABB.getCenter().scale(1/16.0f);
		pose.translate(-centerOffset.x, -centerOffset.y, 0);


		var projectBoxModel = Minecraft.getInstance().getModelManager().getModel(ModParticleModels.PROJECT_BOX);
		var modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		modelRenderer.renderModel(pose.last(), consumer, null, projectBoxModel, 1.0F, 1.0F, 1.0F, 0xF000F0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);

		pose.popPose();
		bufferSource.endBatch();
	}
}
