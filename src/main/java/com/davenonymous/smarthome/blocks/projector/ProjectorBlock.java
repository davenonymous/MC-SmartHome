package com.davenonymous.smarthome.blocks.projector;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.blocks.base.FacingBaseBlock;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.cards.impl.VisualizationCardElement;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.networking.data.VisualizationDataPayload;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.watcher.VizQueryDatabaseTask;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.duckdb.DuckDBConnection;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class ProjectorBlock extends FacingBaseBlock implements EntityBlock {

	public ProjectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		ProjectorBlockEntity entity = (ProjectorBlockEntity) level.getBlockEntity(pos);
		if(entity != null && placer != null) {
			entity.setOwnerUUID(placer.getUUID());
			entity.setChanged();
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if(!(player instanceof ServerPlayer serverPlayer)) {
			return InteractionResult.PASS;
		}

		WorldSavedHomes data = WorldSavedHomes.get((ServerLevel) level);

		ProjectorBlockEntity entity = (ProjectorBlockEntity) level.getBlockEntity(pos);
		if(entity == null) {
			return InteractionResult.PASS;
		}

		UUID homeId = entity.home();
		if(homeId == null) {
			var homes = data.getPlayerHomes(entity.ownerUUID());
			if(!homes.isEmpty()) {
				homeId = homes.getFirst().id();
				entity.setHome(homeId);
				entity.setChanged();
			}
		}
		if(homeId == null) {
			homeId = HomeBlockEntity.emptyUUID;
		}

		var optHome = data.getHome(homeId);
		if(optHome.isEmpty()) {
			return InteractionResult.PASS;
		}

		HomeCore home = optHome.get();
		if(home.cards().isEmpty()) {
			return InteractionResult.PASS;
		}

		var cardId = entity.selectedCard();
		if(cardId == null || home.getCard(cardId).isEmpty()) {
			cardId = home.cards().getFirst().id();
			entity.setSelectedCard(cardId);
		}

		var optCard = home.getCard(cardId);
		if(optCard.isEmpty()) {
			return InteractionResult.PASS;
		}

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
				.map(element -> (VisualizationCardElement)element).toList();

			var worldInfo = HomeWorldInfo.create(home.getHomeLevel(serverPlayer.getServer()), home);
			PacketDistributor.sendToPlayer(serverPlayer, new HomeInfoPayload(home, worldInfo));

			for(var vizCardElement : vizElements) {
				var dbHandler = ModSensors.DB_HANDLERS.get(sensor.id());
				Function<DuckDBConnection, LinkedHashMap<Pair<Instant, Long>, ?>> dbFunction = dbHandler.getValues(deviceId, 0, level.getGameTime());

				var vizId = vizCardElement.vizId();

				// SmartHome.LOGGER.info("Requesting viz data for player='{}' home='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), home.name(), deviceId, sensor.id(), vizId);
				VizQueryDatabaseTask.execute(dbFunction).thenAccept((vizData) -> {
					if(vizData == null) {
						SmartHome.LOGGER.warn("Failed to get viz data for player='{}' home='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), home.name(), deviceId, sensor.id(), vizId);
						return;
					}
					if(vizData.isEmpty()) {
						SmartHome.LOGGER.info("No viz data for player='{}' home='{}' device='{}' sensor='{}' viz='{}'", player.getGameProfile().getName(), home.name(), deviceId, sensor.id(), vizId);
						return;
					}

					//noinspection unchecked
					var replyPayload = new VisualizationDataPayload(device.getSecond(), sensor.id(), vizId, (LinkedHashMap<Pair<Instant, Long>, ISensorData>) vizData);
					PacketDistributor.sendToPlayer(serverPlayer, replyPayload);
				});
			}
		}

		return InteractionResult.SUCCESS_NO_ITEM_USED;
	}

	@Override
	public VoxelShape getShape(Direction facing, AttachFace attachFace) {
		Direction direction = facing;
		if(attachFace== AttachFace.CEILING) {
			direction = Direction.DOWN;
		} else if(attachFace== AttachFace.FLOOR) {
			direction = Direction.UP;
		}
		return Shapes.block();
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new ProjectorBlockEntity(blockPos, blockState);
	}
}
