package com.davenonymous.smarthome.client;



import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.particles.util.BoxLineCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Collection;

public class BoxRenderer {
    private static void renderBlockOutline(PoseStack poseStack, Collection<BoxLineCache.Line> lines, int color, int lineWidth) {


		var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.disableCull();
		RenderSystem.enableBlend();
		var consumer = bufferSource.getBuffer(OverlayLineRenderType.forThickness(lineWidth));
		var pose = poseStack.last();
        for(BoxLineCache.Line line : lines) {
            var sx = line.start().x();
            var sy = line.start().y();
            var sz = line.start().z();
            var dx = line.end().x();
            var dy = line.end().y();
            var dz = line.end().z();

			float r = ((color >> 16) & 0xFF) / 255.0F;
			float g = ((color >> 8) & 0xFF) / 255.0F;
			float b = (color & 0xFF) / 255.0F;
			float a = ((color >> 24) & 0xFF) / 255.0F;

			renderLine(pose, consumer, sx, sy, sz, dx, dy, dz, r, g, b, a, r, g, b);
        }
		RenderSystem.disableBlend();
		RenderSystem.enableCull();
    }

	private static void renderLine(PoseStack.Pose pose, VertexConsumer consumer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red,
		float green, float blue, float alpha, float red2, float green2, float blue2) {
		float f = (float)minX;
		float f1 = (float)minY;
		float f2 = (float)minZ;
		float f3 = (float)maxX;
		float f4 = (float)maxY;
		float f5 = (float)maxZ;

		consumer.addVertex(pose, f, f1, f2).setColor(red2, green2, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f, f1, f5).setColor(red2, green2, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f3, f1, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 1.0F, 0.0F);

		consumer.addVertex(pose, f3, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, -1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, -1.0F, 0.0F, 0.0F);
		consumer.addVertex(pose, f, f4, f2).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
		consumer.addVertex(pose, f, f4, f5).setColor(red, green, blue, alpha).setNormal(pose, 0.0F, 0.0F, 1.0F);
	}

    private static void renderLines(PoseStack poseStack, DyeColor color, Collection<BoxLineCache.Line> lines, int alpha, int lineWidth) {
		Vec3 projection = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		var xOffset = -projection.x;
		var yOffset = -projection.y;
		var zOffset = -projection.z;
		poseStack.pushPose();
		poseStack.translate(xOffset, yOffset, zOffset);
		renderBlockOutline(poseStack, lines, color.getTextColor() | alpha << 24, lineWidth);
		poseStack.popPose();

    }


	public static void onRenderLast(RenderLevelStageEvent event) {
		Player player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		// TODO: Nonononono. This is just for testing, remove it later.
		var overworld = Minecraft.getInstance().getSingleplayerServer().overworld();
		WorldSavedHomes data = WorldSavedHomes.get(overworld);
		if(data.getPlayerHome(player, "F").isEmpty()) {
			return;
		}
		HomeCore home = data.getPlayerHome(player, "F").get();
		home.updateBounds(); // just to be sure
//		var model = ModParticleModels.BLOCK_MARKER_LINE;
//		var modelAABB = ModParticleModels.BLOCK_MARKER_LINE_AABB;
//		var particleShaper = new ParticleShapeHelper(modelAABB);
//		var particlePositions = particleShaper.shape(home.shape());

		BoxLineCache boxLines = new BoxLineCache();
		boxLines.addShape(home.shape());
		BoxRenderer.renderLines(event.getPoseStack(), DyeColor.BLUE, boxLines.lines, 0xAA, 2);
	}
}
