package com.davenonymous.smarthome.setup.event;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.particles.ModelParticleProvider;
import com.davenonymous.smarthome.setup.DeferredRegistries;
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
