package com.davenonymous.smarthome.entities;

import com.davenonymous.smarthome.setup.content.ModParticleModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PlacedProjectBoxRenderer extends EntityRenderer<PlacedProjectBoxEntity> {
	public PlacedProjectBoxRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public ResourceLocation getTextureLocation(PlacedProjectBoxEntity placedProjectBoxEntity) {
		return ModParticleModels.PROJECT_BOX.id();
	}


	@Override
	public void render(PlacedProjectBoxEntity projectBoxEntity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight) {
		return;
	}
}
