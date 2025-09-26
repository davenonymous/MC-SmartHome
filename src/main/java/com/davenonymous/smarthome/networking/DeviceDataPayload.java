package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.SensorData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.gui.HomeScreen;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record DeviceDataPayload(UUID homeId, UUID zoneId, ConfiguredDevice device, List<SensorData> data) implements CustomPacketPayload {
	public static final Type<DeviceDataPayload> TYPE = new Type<>(SmartHome.resource("device_data"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DeviceDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, DeviceDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, DeviceDataPayload::zoneId,
		ConfiguredDevice.STREAM_CODEC, DeviceDataPayload::device,
		SensorData.STREAM_CODEC.apply(ByteBufCodecs.list()), DeviceDataPayload::data,
		DeviceDataPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(DeviceDataPayload payload, IPayloadContext context) {
		var homeScreen = HomeScreen.get();
		if(homeScreen == null) {
			return;
		}

		if(homeScreen.selectedHome == null || !homeScreen.selectedHome.id().equals(payload.homeId())) {
			return;
		}

		homeScreen.setSensorData(payload.device.id(), payload.data());
	}
}
