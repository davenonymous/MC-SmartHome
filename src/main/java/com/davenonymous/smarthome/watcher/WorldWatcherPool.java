package com.davenonymous.smarthome.watcher;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.ISensorData;
import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.config.ServerConfig;
import com.davenonymous.smarthome.watcher.db.*;
import com.davenonymous.smarthome.watcher.db.task.ActionDatabaseTask;
import com.davenonymous.smarthome.watcher.db.task.DatabaseTask;
import com.davenonymous.smarthome.watcher.db.task.QueryDatabaseTask;
import com.davenonymous.smarthome.watcher.db.task.VizQueryDatabaseTask;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.duckdb.DuckDBConnection;

import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;

@EventBusSubscriber(modid = SmartHome.MODID)
public class WorldWatcherPool {
	public static BlockingQueue<DatabaseTask<?>> taskQueue;
	public static DatabaseWorker databaseWorker;
	public static WorldWatcher instance;

	public static final DatabaseTask<?> POISON_PILL = new ActionDatabaseTask(HomeSensor.NOOP);

	public static CompletableFuture<ISensorData> query(Function<DuckDBConnection, ISensorData> query) {
		var task = new QueryDatabaseTask(query);
		return task.enqueue(taskQueue);
	}

	public static CompletableFuture<LinkedHashMap<Pair<Instant, Long>, ?>> fullQuery(Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> query) {
		var task = new VizQueryDatabaseTask(query);
		return task.enqueue(taskQueue);
	}

	public static CompletableFuture<Void> execute(Consumer<DuckDBConnection> action) {
		var task = new ActionDatabaseTask(action);
		return task.enqueue(taskQueue);
	}

	@SubscribeEvent
	public static void onServerStart(ServerStartingEvent event) {
		taskQueue = new LinkedBlockingQueue<>(ServerConfig.taskQueueSize);
		instance = new WorldWatcher(event.getServer());
		var path = event.getServer().getWorldPath(LevelResource.ROOT).resolve(Path.of(ServerConfig.databasePath));
		databaseWorker = new DatabaseWorker(path, taskQueue);
		databaseWorker.setUncaughtExceptionHandler((thread, throwable) -> {
			SmartHome.LOGGER.error("Uncaught exception in database worker thread", throwable);
			throw new RuntimeException(throwable);
		});
		databaseWorker.start();
	}

	@SubscribeEvent
	public static void onServerTick(ServerTickEvent.Post event) {
		if(instance == null) {
			return;
		}

		if(event.getServer().getTickCount() - instance.lastUpdateTick < ServerConfig.minSensorTickRate) {
			return;
		}

		if(ServerConfig.dropSensorsInFavorOfTPS && !event.hasTime()) {
			SmartHome.LOGGER.debug("Skipping sensor update on tick without remaining time");
			return;
		}

		if(taskQueue.remainingCapacity() <= 0) {
			SmartHome.LOGGER.debug("Skipping sensor update, too many tasks in the queue already ({} / {}):", taskQueue.size(), ServerConfig.taskQueueSize);
			Map<Class<?>, Integer> taskCounts = new LinkedHashMap<>();
			for(var task : taskQueue) {
				taskCounts.put(task.getClass(), taskCounts.getOrDefault(task.getClass(), 0) + 1);
			}
			for(var entry : taskCounts.entrySet()) {
				SmartHome.LOGGER.trace(" - {}: {}", entry.getKey().getSimpleName(), entry.getValue());
			}
			return;
		}

		instance.lastUpdateTick = event.getServer().getTickCount();
		List<Consumer<DuckDBConnection>> databaseActions = instance.processHomes();
		for(var action : databaseActions) {
			new ActionDatabaseTask(action).enqueue(taskQueue);
		}
	}

	@SubscribeEvent
	public static void onServerStop(ServerStoppingEvent event) {
		SmartHome.LOGGER.info("Shutting down SmartHome database worker. Remaining tasks: {}", taskQueue.size());
		taskQueue.offer(POISON_PILL);
		instance = null;
	}

	@SubscribeEvent
	public static void onServerStopped(ServerStoppedEvent event) {
		taskQueue = null;
		databaseWorker.close();
	}

}
