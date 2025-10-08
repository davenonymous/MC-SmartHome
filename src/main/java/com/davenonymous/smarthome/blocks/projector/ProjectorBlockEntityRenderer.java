package com.davenonymous.smarthome.blocks.projector;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.base.FacingBaseBlock;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.lib.gui.CellData;
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
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.AttachFace;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectorBlockEntityRenderer implements BlockEntityRenderer<ProjectorBlockEntity> {
	private Map<UUID, Widget> cardWidgets;
	private Map<UUID, Long> widgetUpdateTimes = new HashMap<>();

	public ProjectorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.cardWidgets = new HashMap<>();
		this.widgetUpdateTimes = new HashMap<>();
	}

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
		guigraphics.pose().scale(1/256f, 1/256f, 1/256f);

		int cardWidth = card.width();
		int cardHeight = card.height();

		int wantedSize = 256;


		var blockState = projector.getBlockState();
		var attachFace = blockState.getValue(FacingBaseBlock.ATTACH_FACE);
		var facing = projector.getBlockState().getValue(FacingBaseBlock.HORIZONTAL_FACING);
		var shape = blockState.getShape(projector.getLevel(), projector.getBlockPos());


		var box = shape.bounds().expandTowards(256f, 0, 256f);
		guigraphics.pose().translate(box.getCenter().x, box.getCenter().y, box.getCenter().z);


		guigraphics.pose().mulPose(Axis.XP.rotationDegrees(180));

		Direction from = Direction.NORTH;
		while(from != facing) {
			guigraphics.pose().mulPose(Axis.YP.rotationDegrees(90));
			from = from.getClockWise();
		}



		float scaleFactor = Math.min((float)wantedSize / cardWidth, (float)wantedSize / cardHeight);
		guigraphics.pose().translate(0, -32, 0);
		guigraphics.pose().scale(scaleFactor, scaleFactor, 1);
		guigraphics.pose().translate(-cardWidth / 2f, -cardHeight, 96f);

		if(attachFace == AttachFace.CEILING) {
			guigraphics.pose().mulPose(Axis.XP.rotationDegrees(-90));
			guigraphics.pose().translate(0, 0, 192);
		} else if(attachFace == AttachFace.FLOOR) {
			guigraphics.pose().mulPose(Axis.XP.rotationDegrees(90));
			guigraphics.pose().translate(0, -192, 0);
		}

		long lastUpdate = widgetUpdateTimes.getOrDefault(cardId, 0L);
		long gameTick = projector.getLevel().getGameTime();
		boolean needsUpdate = gameTick % 100 == 0 && lastUpdate != gameTick;
		if(!cardWidgets.containsKey(cardId) ||  needsUpdate) {
			var cardWidget = card.createWidget(false);
			cardWidgets.put(cardId, cardWidget);
			widgetUpdateTimes.put(cardId, gameTick);
		}

		RenderSystem.enableDepthTest();
		cardWidgets.get(cardId).draw(guigraphics, Minecraft.getInstance().getWindow());
		RenderSystem.disableDepthTest();

		guigraphics.pose().popPose();
	}
}
