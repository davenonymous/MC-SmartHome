package com.davenonymous.smarthome.networking.data;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.gui.events.SensorDataUpdatedEvent;
import com.davenonymous.smarthome.networking.ClientCache;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Packet
public record DeviceDataPayload(UUID homeId, UUID zoneId, ConfiguredDevice device, Map<ResourceLocation, ISensorData> data) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, DeviceDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, DeviceDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, DeviceDataPayload::zoneId,
		ConfiguredDevice.STREAM_CODEC, DeviceDataPayload::device,
		ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ISensorData.STREAM_CODEC), DeviceDataPayload::data,
		DeviceDataPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(DeviceDataPayload payload, IPayloadContext context) {
		ClientCache.setSensorData(payload.device.id(), payload.data());

		var homeScreen = HomeScreen.get();
		if(homeScreen == null) {
			return;
		}

		if(homeScreen.selectedHome == null || !homeScreen.selectedHome.id().equals(payload.homeId())) {
			return;
		}

		homeScreen.getOrCreateGui().fireEvent(new SensorDataUpdatedEvent(payload.device.id(), payload.data()));
	}
}
