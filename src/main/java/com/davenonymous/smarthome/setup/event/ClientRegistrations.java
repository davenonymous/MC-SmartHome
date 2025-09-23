package com.davenonymous.smarthome.setup.event;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.entities.PlacedProjectBoxRenderer;
import com.davenonymous.smarthome.items.IHudRenderer;
import com.davenonymous.smarthome.items.IWorldRenderer;
import com.davenonymous.smarthome.particles.ModelParticleProvider;
import com.davenonymous.smarthome.setup.content.ModEntityTypes;
import com.davenonymous.smarthome.setup.content.ModParticles;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = SmartHome.MODID, value = Dist.CLIENT)
public class ClientRegistrations {
	@SubscribeEvent
	public static void onRenderHud(RenderGuiEvent.Post event) {
		if(Minecraft.getInstance().screen != null) {
			// Don't render HUD when a GUI is open
			return;
		}

		var player = Minecraft.getInstance().player;
		if(player == null) {
			return;
		}

		var heldItem = player.getMainHandItem();
		if(heldItem.isEmpty()) {
			return;
		}

		if(heldItem.getItem() instanceof IHudRenderer renderer) {
			renderer.renderHud(event.getGuiGraphics(), Minecraft.getInstance().font, heldItem, event.getPartialTick());
		}
	}

	@SubscribeEvent
	public static void onRenderLast(RenderLevelStageEvent event) {
		var player = Minecraft.getInstance().player;
		if(player == null) {
			return;
		}

		var heldItem = player.getMainHandItem();
		if(heldItem.isEmpty()) {
			return;
		}

		if(heldItem.getItem() instanceof IWorldRenderer renderer) {
			if (event.getStage() != renderer.renderStage()) {
				return;
			}
			renderer.renderWorld(event, heldItem);
		}

	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(ModParticles.MODEL_PARTICLE.get(), new ModelParticleProvider());
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntityTypes.PLACED_PROJECT_BOX_ENTITY_TYPE.get(), PlacedProjectBoxRenderer::new);
	}
}
