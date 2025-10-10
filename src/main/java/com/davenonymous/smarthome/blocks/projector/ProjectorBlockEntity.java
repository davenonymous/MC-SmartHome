package com.davenonymous.smarthome.blocks.projector;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.cards.impl.VisualizationCardElement;
import com.davenonymous.smarthome.config.ClientConfig;
import com.davenonymous.smarthome.config.ServerConfig;
import com.davenonymous.smarthome.data.*;
import com.davenonymous.smarthome.gui.home.main.cards.LoadingWidget;
import com.davenonymous.smarthome.lib.gui.event.WidgetRemovedEvent;
import com.davenonymous.smarthome.lib.gui.widgets.Widget;
import com.davenonymous.smarthome.networking.ClientCache;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.networking.data.VisualizationDataPayload;
import com.davenonymous.smarthome.setup.content.ModBlocks;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.watcher.VizQueryDatabaseTask;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.time.Instant;
import java.util.*;

public class ProjectorBlockEntity extends HomeBlockEntity {
	private UUID selectedCard;
	private TimeRange timeRange;

	// These are client-side only!
	private static Map<UUID, Widget> cardWidgets = new HashMap<>();
	private static Map<UUID, Long> cardUpdateTimes = new HashMap<>();

	// These are server-side only!
	private static Map<UUID, Long> vizDataUpdateTimes = new HashMap<>();

	public ProjectorBlockEntity(BlockPos pos, BlockState blockState) {
		super(ModBlocks.PROJECTOR_ENTITY.get(), pos, blockState);
	}

	public UUID selectedCard() {
		return selectedCard;
	}

	public ProjectorBlockEntity setSelectedCard(UUID selectedCard) {
		if(this.selectedCard != null && this.selectedCard.equals(selectedCard)) {
			return this;
		}

		this.selectedCard = selectedCard;
		this.setChanged();
		return this;
	}

	public ProjectorBlockEntity setTimeRange(TimeRange timeRange) {
		if(this.timeRange != null && this.timeRange.equals(timeRange)) {
			return this;
		}

		this.timeRange = timeRange;
		this.setChanged();
		return this;
	}

	public TimeRange timeRange() {
		return timeRange;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		if(tag.contains("card")) {
			selectedCard = tag.getUUID("card");
		}
		if(tag.contains("timeRange")) {
			timeRange = new TimeRange(tag.getCompound("timeRange"));
		} else {
			timeRange = new TimeRange(TimeRangeEnum.LAST_6_HOURS);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		if(selectedCard != null) {
			tag.putUUID("card", selectedCard);
		}
		if(timeRange != null) {
			tag.put("timeRange", timeRange.writeToNBT());
		}
	}

	public void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
		long gameTick = getLevel().getGameTime();
		boolean needsUpdate = gameTick % ClientConfig.displayRefreshRate == 0;

		Pair<HomeCore, HomeWorldInfo> optHome = ClientCache.INSTANCE.homeCache.get(home());
		if(optHome == null) {
			return;
		}

		var home = optHome.getFirst();
		for(var card : home.cards()) {
			long lastUpdate = cardUpdateTimes.computeIfAbsent(card.id(), k -> gameTick + level.getRandom().nextInt(ClientConfig.displayRefreshRate));
			if(lastUpdate > gameTick - ClientConfig.displayRefreshRate) {
				continue;
			}

			boolean isNew = !cardWidgets.containsKey(card.id());
			if(needsUpdate || isNew) {
				var cardWidget = card.createWidget(false);
				if(!isNew) {
					var oldWidget = cardWidgets.get(card.id());
					oldWidget.fireEvent(new WidgetRemovedEvent(oldWidget));
				}
				cardWidgets.put(card.id(), cardWidget);
				cardUpdateTimes.put(card.id(), gameTick);
			}
		}
	}

	public void serverTick(ServerLevel level, BlockPos blockPos, BlockState blockState) {
		long gameTick = getLevel().getGameTime();
		boolean needsUpdate = gameTick % ServerConfig.displayDataUpdateRate == 0;
		if(!needsUpdate) {
			return;
		}

		WorldSavedHomes data = WorldSavedHomes.get(level);

		UUID homeId = this.home();
		if(homeId == null) {
			var homes = data.getPlayerHomes(this.ownerUUID());
			if(!homes.isEmpty()) {
				homeId = homes.getFirst().id();
				this.setHome(homeId);
				this.setChanged();
			}
		}

		if(homeId == null) {
			homeId = HomeBlockEntity.emptyUUID;
		}

		var optHome = data.getHome(homeId);
		if(optHome.isEmpty()) {
			return;
		}

		HomeCore home = optHome.get();
		if(home.cards().isEmpty()) {
			return;
		}

		var cardId = this.selectedCard();
		if(cardId == null || home.getCard(cardId).isEmpty()) {
			cardId = home.cards().getFirst().id();
			this.setSelectedCard(cardId);
		}

		if(timeRange == null) {
			timeRange = new TimeRange(TimeRangeEnum.LAST_6_HOURS);
			this.setChanged();
		}

		var optCard = home.getCard(cardId);
		if(optCard.isEmpty()) {
			return;
		}

		//var worldInfo = HomeWorldInfo.create(home.getHomeLevel(level.getServer()), home);
		var card = optCard.get();
		for(var entityRef : card.requiredEntities()) {
			var deviceId = entityRef.deviceId();
			var optDevice = home.getDevice(deviceId);
			if(optDevice.isEmpty()) {
				continue;
			}
			var sensor = ModSensors.getById(entityRef.sensorId());
			Pair<HomeZone, ConfiguredDevice> device = optDevice.get();

			List<VisualizationCardElement> vizElements = card.elements().values().stream()
				.map(Pair::getSecond)
				.filter(element -> element instanceof VisualizationCardElement)
				.filter(homeCardElement -> vizDataUpdateTimes.computeIfAbsent(homeCardElement.id(), k -> 0L) < gameTick - ServerConfig.displayDataUpdateRate)
				.map(element -> (VisualizationCardElement)element).toList();

			if(vizElements.isEmpty()) {
				continue;
			}

			PacketDistributor.sendToPlayersNear(level, null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 64, new HomeInfoPayload(home, new HomeWorldInfo(Map.of())));

			var dbHandler = ModSensors.DB_HANDLERS.get(sensor.id());

			var dbFunction = dbHandler.getValues(deviceId, timeRange);
			for(var vizCardElement : vizElements) {

				var vizId = vizCardElement.vizId();
				vizDataUpdateTimes.put(vizCardElement.id(), gameTick);
				VizQueryDatabaseTask.execute(dbFunction).thenAccept((vizData) -> {
					if(vizData == null) {
						SmartHome.LOGGER.warn("Failed to get viz data for home='{}' device='{}' sensor='{}' viz='{}'", home.name(), deviceId, sensor.id(), vizId);
						return;
					}
					if(vizData.isEmpty()) {
						SmartHome.LOGGER.info("No viz data for home='{}' device='{}' sensor='{}' viz='{}'", home.name(), deviceId, sensor.id(), vizId);
						return;
					}

					//noinspection unchecked
					var replyPayload = new VisualizationDataPayload(device.getSecond(), sensor.id(), vizId, (LinkedHashMap<Pair<Instant, Long>, ISensorData>) vizData);
					PacketDistributor.sendToPlayersNear(level, null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 24, replyPayload);
				});
			}
		}
	}

	public Widget getCardWidget(HomeCard card) {
		if(cardWidgets.containsKey(card.id())) {
			return cardWidgets.get(card.id());
		}

		return new LoadingWidget(card.width(), card.height());
	}
}
