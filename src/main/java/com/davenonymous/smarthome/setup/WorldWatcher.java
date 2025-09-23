package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.ISensor;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.AABB;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WorldWatcher implements Runnable {
	public static DuckDBConnection connection;

	private MinecraftServer server;
	private ServerLevel overworld;
	private long lastTick = 0;
	public static final Logger LOGGER = LogUtils.getLogger();

	public WorldWatcher(MinecraftServer server) {
		this.server = server;
		this.overworld = server.overworld();
	}

	public static Iterable<BlockPos> getBlocksInAABBStream(AABB box) {
		int minX = (int)Math.floor(box.minX);
		int minY = (int)Math.floor(box.minY);
		int minZ = (int)Math.floor(box.minZ);
		int maxX = (int)Math.ceil(box.maxX)-1;
		int maxY = (int)Math.ceil(box.maxY)-1;
		int maxZ = (int)Math.ceil(box.maxZ)-1;

		var positions = new LinkedList<BlockPos>();
		for(int x = minX; x <= maxX; x++) {
			for(int y = minY; y <= maxY; y++) {
				for(int z = minZ; z <= maxZ; z++) {
					positions.add(new BlockPos(x, y, z));
				}
			}
		}
		return positions;
	}

	public static List<FoundDevice> searchForDevices(MinecraftServer server, HomeCore home) {
		var level = home.getHomeLevel(server);
		if(level == null) {
			return List.of();
		}

		List<FoundDevice> foundDevices = new ArrayList<>();
		for(var zone : home.zones()) {
			for(var pos : WorldWatcher.getBlocksInAABBStream(zone.bounds())) {
				var state = level.getBlockState(pos);
				if(zone.devices().stream().anyMatch(d -> d.pos().equals(pos) && d.matches(state))) {
					continue;
				}

				if(zone.ignoredDevices().stream().anyMatch(d -> d.pos().equals(pos) && d.matches(state))) {
					continue;
				}

				List<ResourceLocation> foundSensors = new ArrayList<>();
				for(var sensor : ModSensors.SENSORS) {
					if(!sensor.isValid(level, pos, state)) {
						continue;
					}

					foundSensors.add(sensor.id());
				}
				if(foundSensors.isEmpty()) {
					continue;
				}
				foundDevices.add(new FoundDevice(zone.id(), pos, state, foundSensors));
			}
		}

		return foundDevices;
	}

	private void processHome(HomeCore home) throws SQLException{
		var homeLevel = home.getHomeLevel(server);
		if(homeLevel == null) {
			return;
		}

		ModSensors.callVisitHome(connection, homeLevel, home);
		for(var zone : home.zones()) {
			ModSensors.callVisitZone(connection, homeLevel, zone);

			var entities = overworld.getEntitiesOfClass(Entity.class, zone.bounds());
			for(var entity : entities) {
				ModSensors.callVisitZoneEntity(connection, homeLevel, zone, entity);
			}

			for(var device : zone.devices()) {
				var pos = device.pos();
				var blockState = overworld.getBlockState(pos);
				var blockEntity = overworld.getBlockEntity(pos);
				ModSensors.callVisitZoneBlock(connection, homeLevel, zone, pos, blockState, blockEntity);
			}

//			for(var pos : getBlocksInAABBStream(zone.bounds())) {
//				var blockState = overworld.getBlockState(pos);
//				var blockEntity = overworld.getBlockEntity(pos);
//				ModSensors.callVisitZoneBlock(connection, server, zone, pos, blockState, blockEntity);
//			}
		}
	}

	@Override
	public void run() {
		LOGGER.info("Establishing DuckDB connection");
		try {
			connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:" + server.getWorldPath(LevelResource.ROOT).resolve("smarthome.duckdb"));
			ModSensors.createTables(connection);
		} catch (SQLException e) {
			SmartHome.LOGGER.error("Error initializing DuckDB", e);
			throw new RuntimeException(e);
		}

		LOGGER.info("Entering world watcher loop");
		while(true) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				break;
			}

			if(lastTick == server.getTickCount()) {
				continue;
			}

			lastTick = server.getTickCount();

			var homes = WorldSavedHomes.get(overworld);
			for(var home : homes.homes().values()) {
				try {
					processHome(home);
				} catch (SQLException e) {
					SmartHome.LOGGER.error("Error processing home for player='{}' home='{}'", home.owner(), home.name(), e);
				}
			}

		}

		LOGGER.info("Exiting world watcher loop, closing DuckDB connection");
		try {
			connection.close();
		} catch (SQLException e) {
			SmartHome.LOGGER.error("Error closing DuckDB connection", e);
		}

		LOGGER.info("Stopping world watcher thread");
	}
}
