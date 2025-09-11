package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.UUID;

public class WorldWatcher implements Runnable {

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

	private void processHome(UUID player, HomeCore home) {
		for(var zone : home.zones()) {
			var entities = overworld.getEntitiesOfClass(LivingEntity.class, zone.shape());
			for(var entity : entities) {
				if(entity.getUUID().equals(player)) {
					LOGGER.info("Player {} is in home {} zone {}", player, home.name(), zone.name());
				}
			}
			for(var pos : getBlocksInAABBStream(zone.shape())) {
				var blockState = overworld.getBlockState(pos);
				// Do something with the block state, e.g. check if it's a specific type
				//LOGGER.info("Block at {}: {}", pos, blockState.getBlock().getName().getString());
			}
		}
	}

	@Override
	public void run() {
		LOGGER.info("Starting world watcher thread");
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
			//LOGGER.info("Tick: {}", lastTick);

			var homes = WorldSavedHomes.get(overworld);
			for(var owner : homes.playerHomes().keySet()) {
				//LOGGER.info("Player {} has {} homes", owner, homes.playerHomes().get(owner).size());
				var homeList = homes.playerHomes().get(owner);
				for(var home : homeList) {
					processHome(owner, home);
				}
			}
		}
		LOGGER.info("Stopping world watcher thread");
	}
}
