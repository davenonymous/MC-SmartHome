package com.davenonymous.smarthome.setup.event;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.particles.ModelParticleProvider;
import com.davenonymous.smarthome.setup.content.ModParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = SmartHome.MODID, value = Dist.CLIENT)
public class ClientRegistrations {
	@SubscribeEvent
	public static void onRenderLast(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
			//BoxRenderer.onRenderLast(event);
		}
	}

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(ModParticles.MODEL_PARTICLE.get(), new ModelParticleProvider());
	}
}
