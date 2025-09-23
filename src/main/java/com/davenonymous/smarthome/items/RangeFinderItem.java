package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.client.OverlayLineRenderType;
import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.networking.actions.SetRangeFinderPosition;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.davenonymous.smarthome.setup.content.ModParticleModels;
import com.davenonymous.smarthome.setup.content.ModParticles;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.Optional;

@EventBusSubscriber(modid = SmartHome.MODID)
public class RangeFinderItem extends Item implements IHudRenderer, IWorldRenderer {
	public RangeFinderItem() {
		super(new Properties());
	}

	@Override
	public void renderWorld(RenderLevelStageEvent event, ItemStack heldItem) {
		var rangeFinderData = heldItem.get(ModDataComponents.RANGER_FINDER_DATA_COMPONENT);
		if(rangeFinderData == null) {
			return;
		}

		var posA = rangeFinderData.A();
		var posB = rangeFinderData.B();


		var blueModel = Minecraft.getInstance().getModelManager().getModel(ModParticleModels.CORNER_MARKER);
		var orangeModel = Minecraft.getInstance().getModelManager().getModel(ModParticleModels.CORNER_MARKER_ORANGE);

		var modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
		var pose = event.getPoseStack();
		var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.disableCull();
		RenderSystem.enableBlend();

		var consumer = bufferSource.getBuffer(RenderType.SOLID);

		pose.pushPose();
		var camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		pose.translate(-camPos.x, -camPos.y, -camPos.z);


		// By default the model faces south (zp) east (xp) and up (yp)
		pose.pushPose();
		pose.translate(posA.getX(), posA.getY(), posA.getZ());
		pose.translate(0.5, 0.5, 0.5);

		// BLUE
		if(posA.getZ() >= posB.getZ()) {
			// posA is south of posB
			pose.mulPose(Axis.XN.rotationDegrees(90));

			if(posA.getY() >= posB.getY()) {
				// posA is above posB
				pose.mulPose(Axis.XN.rotationDegrees(90));
			}
		} else {
			if(posA.getY() >= posB.getY()) {
				// posA is above posB
				pose.mulPose(Axis.XP.rotationDegrees(90));
			}
		}

		if(posA.getX() >= posB.getX() ) {
			// posA is east of posB
			pose.mulPose(Axis.ZP.rotationDegrees(90));
		}

		pose.translate(-0.5, -0.5, -0.5);
		modelRenderer.renderModel(pose.last(), consumer, null, blueModel, 1.0F, 1.0F, 1.0F, 0xF000F0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
		pose.popPose();

		pose.pushPose();
		pose.translate(posB.getX(), posB.getY(), posB.getZ());
		pose.translate(0.5, 0.5, 0.5);


		// ORANGE
		if(posA.getZ() < posB.getZ()) {
			// posA is south of posB
			pose.mulPose(Axis.XN.rotationDegrees(90));

			if(posA.getY() < posB.getY()) {
				// posA is above posB
				pose.mulPose(Axis.XN.rotationDegrees(90));
			}
		} else {
			if(posA.getY() < posB.getY()) {
				// posA is above posB
				pose.mulPose(Axis.XP.rotationDegrees(90));
			}
		}

		if(posA.getX() < posB.getX() ) {
			// posA is east of posB
			pose.mulPose(Axis.ZP.rotationDegrees(90));
		}

		pose.translate(-0.5, -0.5, -0.5);
		modelRenderer.renderModel(pose.last(), consumer, null, orangeModel, 1.0F, 1.0F, 1.0F, 0xF000F0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);
		pose.popPose();

		pose.popPose();

		RenderSystem.disableBlend();
		RenderSystem.enableCull();
	}

	@Override
	public void renderHud(GuiGraphics guiGraphics, Font font, ItemStack heldItem, DeltaTracker partialTick) {
		int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

		var rangeFinderData = heldItem.get(ModDataComponents.RANGER_FINDER_DATA_COMPONENT);

		RangeFinderHud hud = new RangeFinderHud(screenWidth, screenHeight, rangeFinderData);
		hud.draw(guiGraphics, Minecraft.getInstance().getWindow());
	}

	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
		if(event.getItemStack().isEmpty()) {
			return;
		}

		if(!(event.getItemStack().getItem() instanceof RangeFinderItem rangeFinderItem)) {
			return;
		}

		event.setCanceled(true);

		if(!event.getSide().isClient()) {
			// Client-side only, no need to check server-side conditions
			return;
		}

		var player = event.getEntity();
		var level = event.getLevel();
		var pos = event.getPos();
		var face = event.getFace();
		var smartPos = pos;
		if(face != null) {
			smartPos = pos.relative(face);
		}
		rangeFinderItem.updateFirstPosition(level, player, smartPos);
	}

	@SubscribeEvent
	public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
		if(event.getItemStack().isEmpty()) {
			return;
		}

		if(!(event.getItemStack().getItem() instanceof RangeFinderItem rangeFinderItem)) {
			return;
		}

		// This event is client-side only, we need to tell the server ourselves

		var player = event.getEntity();
		var level = event.getLevel();

		BlockHitResult blockHit = getPlayerPOVHitResult(event.getLevel(), player, ClipContext.Fluid.ANY, 48.0f);
		if(blockHit.getType() != BlockHitResult.Type.BLOCK) {
			return;
		}

		var pos = blockHit.getBlockPos();
		var face = blockHit.getDirection();
		var smartPos = pos.relative(face);
		rangeFinderItem.updateFirstPosition(level, player, smartPos);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		var level = context.getLevel();
		var player = context.getPlayer();

		if(!level.isClientSide()) {
			// Client-side only, no need to check server-side conditions
			return InteractionResult.PASS;
		}

		var pos = context.getClickedPos();
		var face = context.getClickedFace();
		var smartPos = pos.relative(face);
		updateSecondPosition(level, player, smartPos);
		return InteractionResult.SUCCESS;
	}

	private void updateFirstPosition(Level level, Player player, BlockPos pos) {
		PacketDistributor.sendToServer(new SetRangeFinderPosition(pos, false));
	}

	private void updateSecondPosition(Level level, Player player, BlockPos pos) {
		PacketDistributor.sendToServer(new SetRangeFinderPosition(pos, true));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		var itemStack = player.getItemInHand(hand);
		if(hand == InteractionHand.OFF_HAND) {
			// Client-side only, no need to check server-side conditions
			return InteractionResultHolder.pass(itemStack);
		}

		if(!(itemStack.getItem() instanceof RangeFinderItem)) {
			return InteractionResultHolder.pass(itemStack);
		}

		if(!level.isClientSide()) {
			// Client-side only, no need to check server-side conditions
			return InteractionResultHolder.pass(itemStack);
		}

		BlockHitResult blockHit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY, 48.0f);
		if(blockHit.getType() != BlockHitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemStack);
		}

		var pos = blockHit.getBlockPos();
		var face = blockHit.getDirection();
		var smartPos = pos.relative(face);
		updateSecondPosition(level, player, smartPos);

		return InteractionResultHolder.success(itemStack);
	}

	public static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode, float range) {
		Vec3 vec3 = player.getEyePosition();
		Vec3 vec31 = vec3.add(player.calculateViewVector(player.getXRot(), player.getYRot()).scale(range));
		return level.clip(new ClipContext(vec3, vec31, net.minecraft.world.level.ClipContext.Block.OUTLINE, fluidMode, player));
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		var result = new VBoxTooltipComponent();

		result.add(WrappedStringTooltipComponent.gray("Uses a laser to determine the distance to objects."));
		result.add(WrappedStringTooltipComponent.yellow("Use to create zones for your smart home."));

		return Optional.of(result);
	}

}
