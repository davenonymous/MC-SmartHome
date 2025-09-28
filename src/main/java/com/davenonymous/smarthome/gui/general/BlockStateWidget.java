package com.davenonymous.smarthome.gui.general;

import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class BlockStateWidget extends Widget {
	private BlockState state;
	private BakedModel model;

	public BlockStateWidget(BlockState state) {
		super();
		setSize(16, 16);
		setState(state);
	}

	public BlockStateWidget setState(BlockState state) {
		this.state = state;
		this.model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
		return this;
	}

	@Override
	public void draw(GuiGraphics pGuiGraphics, Window window) {
		super.draw(pGuiGraphics, window);

		if(model == null || state == null) {
			return;
		}

		var level = Minecraft.getInstance().level;
		var modelRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

		var poseStack = pGuiGraphics.pose();
		poseStack.pushPose();
		poseStack.translate(width / 2f, height / 2f, 150f);
		poseStack.translate(0f, 2.5f, 0f);
		poseStack.scale(width, -height, width);
		poseStack.scale(0.6f, 0.6f, 0.6f);

		poseStack.rotateAround(Axis.XP.rotationDegrees(30f), 1.0f, 0, 0);
		poseStack.rotateAround(Axis.YP.rotationDegrees(45f), 0, 0f, 1);

		var renderTypeOptions = model.getRenderTypes(state, level.getRandom(), ModelData.EMPTY);
		var renderType = renderTypeOptions.isEmpty() ? RenderType.SOLID : renderTypeOptions.iterator().next();

		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

		Lighting.setupFor3DItems();
		modelRenderer.tesselateWithAO(level, model, state, BlockPos.ZERO, poseStack, pGuiGraphics.bufferSource().getBuffer(renderType), false, level.getRandom(), 42, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
		//modelRenderer.renderModel(poseStack.last(), pGuiGraphics.bufferSource().getBuffer(renderType), state, model, 1f, 1f, 1f, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);


		poseStack.popPose();
		pGuiGraphics.flush();
	}
}
