package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.machinezoo.noexception.throwing.ThrowingConsumer;
import com.machinezoo.noexception.throwing.ThrowingFunction;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.duckdb.DuckDBConnection;

import java.sql.ResultSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@EventBusSubscriber(modid = SmartHome.MODID)
public class WorldWatcherPool {
	public static BlockingQueue<DatabaseTask> taskQueue;
	public static DatabaseWorker databaseWorker;
	public static WorldWatcher instance;

	public static final DatabaseTask POISON_PILL = new DatabaseTask(ISensor.NOOP);

	public static CompletableFuture<ResultSet> query(ThrowingFunction<DuckDBConnection, ResultSet> query) {
		CompletableFuture<ResultSet> future = new CompletableFuture<>();
		taskQueue.offer(new FutureDatabaseTask(query, future));
		return future;
	}

	public static CompletableFuture<Void> execute(ThrowingConsumer<DuckDBConnection> action) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		taskQueue.offer(new FutureDatabaseTask(action, future));
		return future;
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event) {
		taskQueue = new LinkedBlockingQueue<>();
		instance = new WorldWatcher(event.getServer());
		var path = event.getServer().getWorldPath(LevelResource.ROOT).resolve("smarthome.duckdb");
		databaseWorker = new DatabaseWorker(path, taskQueue);
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
