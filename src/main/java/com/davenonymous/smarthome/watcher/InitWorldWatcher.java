package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

@EventBusSubscriber(modid = SmartHome.MODID)
public class InitWorldWatcher {
	public static BlockingQueue<DatabaseTask> taskQueue;
	public static DatabaseWorker databaseWorker;
	public static WorldWatcher instance;

	public static final DatabaseTask POISON_PILL = new DatabaseTask(ISensor.NOOP);

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event) {
		taskQueue = new LinkedBlockingQueue<>();
		instance = new WorldWatcher(event.getServer());
		databaseWorker = new DatabaseWorker(event.getServer(), taskQueue);
		databaseWorker.start();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		if(instance == null) {
			return;
		}

		var databaseActions = instance.processHomes();
		for(var action : databaseActions) {
			taskQueue.offer(new DatabaseTask(action));
		}
	}

	@SubscribeEvent
	public static void onServerStop(ServerStoppingEvent event) {
		taskQueue.offer(POISON_PILL);
		instance = null;
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		taskQueue = null;
	}
}
