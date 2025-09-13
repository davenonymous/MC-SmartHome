package com.davenonymous.smarthome.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL33;

public class ModelParticle extends Particle {
	BakedModel model;
	ModelParticleOptions options;
	Vec3 position;

	public ModelParticle(ClientLevel level, BakedModel model, Vec3 position, ModelParticleOptions options) {
		super(level, position.x, position.y, position.z);
		this.model = model;
		this.options = options;
		this.position = position;

		this.lifetime = options.lifetime();
		this.alpha = 1.0f;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
		var mc = Minecraft.getInstance();
		PoseStack poseStack = new PoseStack();
		poseStack.pushPose();
		var camPos = camera.getPosition();
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		Quaternionf quatty = new Quaternionf();
		poseStack.translate(this.x, this.y, this.z);
		for(var axis : this.options.rotationAxis()) {
			if(axis == null) {
				continue;
			}

			quatty.mul(axis.rotationDegrees(90));
		}
		poseStack.mulPose(quatty);

		// move to block center, i.e. our model is now exactly in the 16x16x16 cube of a block
		poseStack.translate(-0.5f, -0.5f, -0.5f);

		// Actually center the model:
		var offset = new Vec3(16, 16, 16).subtract(options.rotationOrigin()).scale(0.5f / 16f);
		poseStack.translate(offset.x, offset.y, offset.z);

		// options.rotationOrigin()
		//poseStack.translate(-rotationOrigin.x, -rotationOrigin.y, -rotationOrigin.z);

		boolean renderThroughWalls = false;
		if(renderThroughWalls) {
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(GL33.GL_LEQUAL);
		}
		RenderSystem.enableBlend();
		mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer, null, model, 1.0f, 1.0f, 1.0f, 0xF000F0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.TRIPWIRE);

		RenderSystem.disableBlend();
		if(renderThroughWalls) {
			RenderSystem.disableDepthTest();
		}
		poseStack.popPose();
	}

	public static final ParticleRenderType PARTICLE_RENDER_TYPE = new ParticleRenderType() {
		public BufferBuilder begin(Tesselator p_350576_, TextureManager textureManager) {
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
			RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);

			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
			return p_350576_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
		}

		public String toString() {
			return "MODEL_PARTICLE_RENDER_TYPE";
		}

		public boolean isTranslucent() {
			return false;
		}
	};

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.TERRAIN_SHEET;
	}
}
