package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.particles.ModelParticleProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = SmartHome.MODID, value = Dist.CLIENT)
public class ClientRegistrations {

	@SubscribeEvent
	public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
		event.registerSpecial(DeferredRegistries.MODEL_PARTICLE.get(), new ModelParticleProvider());
	}
}
