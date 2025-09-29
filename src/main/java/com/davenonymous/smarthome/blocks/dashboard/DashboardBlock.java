package com.davenonymous.smarthome.blocks.dashboard;

import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.blocks.base.FacingBaseBlock;
import com.davenonymous.smarthome.blocks.base.HomeBlockEntity;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.OpenHomeScreenPayload;
import com.davenonymous.smarthome.networking.TestPacketDataPayload;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DashboardBlock extends FacingBaseBlock implements EntityBlock {
	private static Map<Direction, VoxelShape> SHAPES = Map.of(
		Direction.NORTH, Shapes.box(0.1875, 0.0625, 0, 0.8125, 0.6875, 0.3125),
		Direction.SOUTH, Shapes.box(0.1875, 0.0625, 1-0.3125, 0.8125, 0.6875, 1),
		Direction.EAST,  Shapes.box(1-0.3125, 0.0625, 0.1875, 1, 0.6875, 0.8125),
		Direction.WEST,  Shapes.box(0, 0.0625, 0.1875, 0.3125, 0.6875, 0.8125),
		Direction.UP,    Shapes.box(0.1875, 1-0.3125, 0.1875, 0.8125, 1, 0.8125),
		Direction.DOWN,  Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.3125, 0.8125)
	);

	public DashboardBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);

		DashboardBlockEntity entity = (DashboardBlockEntity) level.getBlockEntity(pos);
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

		DashboardBlockEntity entity = (DashboardBlockEntity) level.getBlockEntity(pos);
		if(entity == null) {
			return InteractionResult.PASS;
		}

		UUID homeId = entity.home();
		HomeWorldInfo worldInfo = HomeWorldInfo.empty();
		if(homeId == null) {
			homeId = HomeBlockEntity.emptyUUID;
		} else {
			var optHome = data.getHome(homeId);
			if(optHome.isPresent()) {
				var home = optHome.get();
				var foundDevices = WorldWatcherUtil.searchForDevices(level.getServer(), home);
				home.setFoundDevices(foundDevices);

				for(var zone : home.zones()) {
					List<ConfiguredDevice> newDeviceList = new ArrayList<>();
					for(var device : zone.devices()) {
						// Update the device's sensors with any new sensors that might be available
						// This can happen when new sensors are added by other mods, or when the block
						// at the device's position has changed to a different block that supports
						// different sensors.
						Map<ResourceLocation, SensorSettings> foundSensors = new HashMap<>(device.sensors());
						for(var sensor : ModSensors.getValidSensors(level, device.pos(), level.getBlockState(device.pos()))) {
							var settings = foundSensors.get(sensor.id());
							if(settings == null) {
								settings = sensor.getDefaultSettings();
							}
							foundSensors.put(sensor.id(), settings);
						}

						device = device.withSensors(foundSensors);
						newDeviceList.add(device);
					}

					zone.updateDevices(newDeviceList);
				}

				data.setDirty();
				worldInfo = HomeWorldInfo.create((ServerLevel) level, home);
			}
		}

		PacketDistributor.sendToPlayer(serverPlayer, new TestPacketDataPayload("Sup, ma boi"));
		PacketDistributor.sendToPlayer(serverPlayer, new OpenHomeScreenPayload(pos, homeId, data.getPlayerHomes(entity.ownerUUID()), worldInfo));
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
		return SHAPES.get(direction);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new DashboardBlockEntity(blockPos, blockState);
	}
}
