package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@EventBusSubscriber(modid = SmartHome.MODID)
public class InitWorldWatcher {
	public static Thread workerThread;

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event) {
		workerThread = new Thread(new WorldWatcher(event.getServer()));
		workerThread.setName("SmartHome-WorldWatcher");
		workerThread.start();
	}

	@SubscribeEvent
	public static void onServerStop(ServerStoppingEvent event) {
		workerThread.interrupt();
		workerThread = null;
	}
}
