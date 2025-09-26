package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.content.ModSensors;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record AddDevicePayload(UUID homeId, UUID zone, ConfiguredDevice device)  implements CustomPacketPayload {
	public static final Type<AddDevicePayload> TYPE = new Type<>(SmartHome.resource("add_device"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AddDevicePayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, AddDevicePayload::homeId,
		UUIDUtil.STREAM_CODEC, AddDevicePayload::zone,
		ConfiguredDevice.STREAM_CODEC, AddDevicePayload::device,
		AddDevicePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(AddDevicePayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
		ServerLevel level = (ServerLevel) player.level();

		var homes = WorldSavedHomes.get(level);
		var optHome = homes.getHome(payload.homeId());
		if(optHome.isEmpty()) {
			return;
		}

		var home = optHome.get();
		if(!home.owner().equals(player.getUUID())) {
			return;
		}

		var optZone = home.getZone(payload.zone());
		if(optZone.isEmpty()) {
			return;
		}

		var zone = optZone.get();
		var device = payload.device();
		var state = level.getBlockState(device.pos());
		List<SensorSettings> foundSensors = new ArrayList<>();
		for(var sensor : ModSensors.SENSORS) {
			if(!sensor.isValid(level, device.pos(), state)) {
				continue;
			}

			foundSensors.add(sensor.getDefaultSettings());
		}
		var fullDataDevice = device.withSensors(foundSensors);
		zone.addDevice(fullDataDevice);
		homes.setDirty();
		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
