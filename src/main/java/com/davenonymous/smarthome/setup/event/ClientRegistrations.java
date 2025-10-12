package com.davenonymous.smarthome.setup.event;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.blocks.projector.ProjectorBlockEntityRenderer;
import com.davenonymous.smarthome.config.ServerConfig;
import com.davenonymous.smarthome.content.items.IHudRenderer;
import com.davenonymous.smarthome.content.items.IWorldRenderer;
import com.davenonymous.smarthome.util.particles.ModelParticleProvider;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.content.ModParticles;
import com.davenonymous.smarthome.watcher.WorldWatcherPool;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = SmartHome.MODID, value = Dist.CLIENT)
public class ClientRegistrations {

	@SubscribeEvent
	public static void onDebugScreen(CustomizeGuiOverlayEvent.DebugText event) {
		if(WorldWatcherPool.taskQueue == null) {
			return;
		}

		var left = event.getLeft();
		left.add("SmartHome Database Queue: " + WorldWatcherPool.taskQueue.size() + "/" + ServerConfig.taskQueueSize);
	}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		SmartHome.CONTAINER.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	@SubscribeEvent
	public static void onScreenOpen(ScreenEvent.Opening event) {
		if(event.getScreen() instanceof TitleScreen) {
			if(!FMLEnvironment.production) {
				SmartHome.LOGGER.info("SmartHome is running in a development environment");
				Window window = Minecraft.getInstance().getWindow();
				window.setWindowed(1920, 1080);
				window.windowedX = 3440 - 1920 - 1 ;
				window.windowedY = 32;
				window.setWindowed(1920, 1080);
			}
		}
	}

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

		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
			return;
		}

		if(heldItem.getItem() instanceof IWorldRenderer renderer) {
			renderer.renderWorld(event, heldItem);
		}

	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(ModParticles.MODEL_PARTICLE.get(), new ModelParticleProvider());
	}

	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(ModBlocks.PROJECTOR_ENTITY.get(), ProjectorBlockEntityRenderer::new);
	}
}
