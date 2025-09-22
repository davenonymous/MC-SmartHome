package com.davenonymous.smarthome.items;

import com.davenonymous.smarthome.lib.gui.tooltip.VBoxTooltipComponent;
import com.davenonymous.smarthome.lib.gui.tooltip.WrappedStringTooltipComponent;
import com.davenonymous.smarthome.setup.content.ModParticleModels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.Optional;

public class ProjectBoxItem extends Item implements IWorldRenderer {
	public ProjectBoxItem() {
		super(new Properties());
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		var result = new VBoxTooltipComponent();

		result.add(WrappedStringTooltipComponent.gray("Used to make devices smart."));
		result.add(WrappedStringTooltipComponent.yellow("Insert a microcontroller and appropriate sensors, actuators or signal converters."));

		return Optional.of(result);
	}

	@Override
	public RenderLevelStageEvent.Stage renderStage() {
		return RenderLevelStageEvent.Stage.AFTER_PARTICLES;
	}

	@Override
	public void renderWorld(RenderLevelStageEvent event, ItemStack heldItem) {
		var level = Minecraft.getInstance().level;
		var player = Minecraft.getInstance().player;
		BlockHitResult blockHit = RangeFinderItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY, 48.0f);
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
