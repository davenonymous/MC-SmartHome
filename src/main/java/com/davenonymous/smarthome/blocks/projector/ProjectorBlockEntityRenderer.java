package com.davenonymous.smarthome.blocks.projector;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.networking.ClientCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class ProjectorBlockEntityRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
	public ProjectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		SmartHome.LOGGER.info("ProjectorBlockEntityRenderer initialized");
	}

	private Widget deleteMe = null;

	@Override
	public void render(ProjectorBlockEntity projector, float partialTicks, PoseStack pose, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
		if(projector.selectedCard() == null) {
			return;
		}

		UUID homeId = projector.home();
		var optHome = ClientCache.getHome(homeId);
		if(optHome.isEmpty()) {
			// TODO: Request home info
			return;
		}

		HomeCore home = optHome.get().getFirst();
		if(home.cards().isEmpty()) {
			return;
		}

		var cardId = projector.selectedCard();
		var optCard = home.getCard(cardId);
		if(optCard.isEmpty()) {
			return;
		}

		var card = optCard.get();
		GuiGraphics guigraphics = new GuiGraphics(Minecraft.getInstance(), (MultiBufferSource.BufferSource) bufferSource);
		guigraphics.pose().pushPose();
		guigraphics.pose().mulPose(pose.last().pose());
		guigraphics.pose().scale(1/16f, 1/16f, 1/16f);
		guigraphics.pose().translate(0, 16, 0);
		guigraphics.pose().scale(1/16f, 1/16f, 1/16f);
		guigraphics.pose().scale(1.5f, 1.5f, 1.5f);
		guigraphics.pose().mulPose(Axis.XP.rotationDegrees(180));
		guigraphics.pose().mulPose(Axis.YP.rotationDegrees(180));
		if(deleteMe == null || projector.getLevel().getGameTime() % 100 == 0) {
			deleteMe = card.createWidget(false);
		}

		//pose.pushPose();
		//pose.mulPose(RenderSystem.getModelViewMatrix());
		//pose.scale(16.0f, -16.0f, 16.0f);
		//pose.translate(projector.getBlockPos().getX() + 0.5, projector.getBlockPos().getY() + 1.0, projector.getBlockPos().getZ() + 0.5);
		//pose.mulPose(RenderSystem.getProjectionMatrix());
		//deleteMe.draw(guigraphics, Minecraft.getInstance().getWindow());

		//pose.translate(0, 2, 0);
		RenderSystem.disableDepthTest();
		deleteMe.draw(guigraphics, Minecraft.getInstance().getWindow());

		//pose.popPose();
		guigraphics.pose().popPose();
	}
}
