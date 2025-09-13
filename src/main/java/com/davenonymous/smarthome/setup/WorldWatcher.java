package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.setup.content.ModSensors;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.duckdb.DuckDBConnection;
import org.slf4j.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

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

	private Iterable<BlockPos> getBlocksInAABBStream(AABB box) {
		int minX = (int)Math.floor(box.minX);
		int minY = (int)Math.floor(box.minY);
		int minZ = (int)Math.floor(box.minZ);
		int maxX = (int)Math.ceil(box.maxX);
		int maxY = (int)Math.ceil(box.maxY);
		int maxZ = (int)Math.ceil(box.maxZ);

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

	private void processHome(HomeCore home) throws SQLException{
		ModSensors.callVisitHome(connection, server, home);
		for(var zone : home.zones()) {
			ModSensors.callVisitZone(connection, server, zone);

			var entities = overworld.getEntitiesOfClass(Entity.class, zone.bounds());
			for(var entity : entities) {
				ModSensors.callVisitHomeEntity(connection, server, home, entity);
				ModSensors.callVisitZoneEntity(connection, server, zone, entity);
			}

			for(var pos : getBlocksInAABBStream(zone.bounds())) {
				var blockState = overworld.getBlockState(pos);
				var blockEntity = overworld.getBlockEntity(pos);
				ModSensors.callVisitHomeBlock(connection, server, home, pos, blockState, blockEntity);
				ModSensors.callVisitZoneBlock(connection, server, zone, pos, blockState, blockEntity);
			}
		}
	}

	@Override
	public void run() {
		LOGGER.info("Establishing DuckDB connection");
		try {
			connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:persistent.duckdb");
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
			for(var owner : homes.playerHomes().keySet()) {
				var homeList = homes.playerHomes().get(owner);
				for(var home : homeList) {
					try {
						processHome(home);
					} catch (SQLException e) {
						SmartHome.LOGGER.error("Error processing home for player='{}' home='{}'", owner, home.name(), e);
					}
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
