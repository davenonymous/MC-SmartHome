package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.DeviceDataPayload;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
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
import java.util.concurrent.CompletableFuture;

public record RequestDeviceDataPayload(UUID homeId, UUID zone, ConfiguredDevice device)  implements CustomPacketPayload {
	public static final Type<RequestDeviceDataPayload> TYPE = new Type<>(SmartHome.resource("request_device_data"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RequestDeviceDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, RequestDeviceDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, RequestDeviceDataPayload::zone,
		ConfiguredDevice.STREAM_CODEC, RequestDeviceDataPayload::device,
		RequestDeviceDataPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(RequestDeviceDataPayload payload, IPayloadContext context) {
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

		CompletableFuture<ISensorData>[] futures = new CompletableFuture[device.sensors().size()];
		int i = 0;
		for(SensorSettings sensorSettings : device.sensors().values()) {
			futures[i++] = WorldWatcherUtil.getSensorState(zone, device, sensorSettings);
		}

		CompletableFuture.allOf(futures).thenRun(() -> {
			List<ISensorData> sensorData = new ArrayList<>();
			for(var watcherFuture : futures) {
				try {
					var data = watcherFuture.get();
					if(data != null) {
						sensorData.add(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			context.reply(new DeviceDataPayload(payload.homeId, payload.zone, payload.device, sensorData));
		});

	}
}
