package com.davenonymous.smarthome.setup.event;


import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.content.ModParticleModels;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = SmartHome.MODID)
public class RegisterModelsHandler {

	@SubscribeEvent // on the mod event bus only on the physical client
	public static void registerAdditional(ModelEvent.RegisterAdditional event) {
		event.register(ModParticleModels.BLOCK_MARKER_LINE);
	}
}
