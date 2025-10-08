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
		float scaleFactor = Math.min((float)wantedSize / cardWidth, (float)wantedSize / cardHeight);
		guigraphics.pose().scale(scaleFactor, scaleFactor, scaleFactor);

		if(cardWidth > cardHeight) {
			int yOffset = (wantedSize - (int)(cardHeight * scaleFactor)) / 2;
			guigraphics.pose().translate(0, yOffset, 0);
		} else {
			int xOffset = (wantedSize - (int)(cardWidth * scaleFactor)) / 2;
			guigraphics.pose().translate(xOffset, 0, 0);
		}
		guigraphics.pose().translate(cardWidth, cardHeight + 32, 264);

		guigraphics.pose().mulPose(Axis.XP.rotationDegrees(180));
		guigraphics.pose().mulPose(Axis.YP.rotationDegrees(180));
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
