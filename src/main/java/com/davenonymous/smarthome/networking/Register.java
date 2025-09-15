package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SmartHome.MODID)
public class Register {
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");

		registrar.playToClient(
			HomeInfoPayload.TYPE,
			HomeInfoPayload.CODEC,
			HomeInfoPayload::handleOnClient
		);
	}
}
