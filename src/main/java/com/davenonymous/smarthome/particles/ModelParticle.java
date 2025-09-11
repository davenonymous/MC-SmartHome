package com.davenonymous.smarthome.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
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

import java.util.List;

public class ModelParticle extends Particle {
	BakedModel model;
	List<Axis> rotationAxis;
	Vec3 rotationOrigin;

	public ModelParticle(ClientLevel level, BakedModel model, Vec3 position, Vec3 rotOrigin, List<Axis> rotationAxis, int lifetime) {
		super(level, position.x, position.y, position.z);
		this.model = model;
		this.lifetime = lifetime;
		this.alpha = 1.0f;
		this.rotationAxis = rotationAxis;
		this.rotationOrigin = rotOrigin;
	}

	@Override
	public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
		var mc = Minecraft.getInstance();
		PoseStack poseStack = new PoseStack();
		poseStack.pushPose();
		var camPos = camera.getPosition();
		poseStack.translate(-camPos.x, -camPos.y, -camPos.z);

		Quaternionf quatty = new Quaternionf();
		poseStack.translate(this.x, this.y, this.z);
		for(var axis : this.rotationAxis) {
			if(axis == null) {
				continue;
			}

			quatty.mul(axis.rotationDegrees(90));
		}
		poseStack.mulPose(quatty);
		poseStack.translate(-0.5f, -0.5f, -0.5f);
		poseStack.translate(5.5f/16, 7.5f/16, 6.5f/16);

		//poseStack.translate(-rotationOrigin.x, -rotationOrigin.y, -rotationOrigin.z);

		mc.getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer, null, model, 1.0f, 1.0f, 1.0f, 0xF000F0, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.SOLID);

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
