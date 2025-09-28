package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.gui.HomeScreen;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

public record VisualizationDataPayload(UUID homeId, UUID zoneId, ConfiguredDevice device, ResourceLocation sensorId, ResourceLocation vizId, IVisualizationData data) implements CustomPacketPayload {
	public static final Type<VisualizationDataPayload> TYPE = new Type<>(SmartHome.resource("viz_data"));

	public static final StreamCodec<RegistryFriendlyByteBuf, VisualizationDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, VisualizationDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, VisualizationDataPayload::zoneId,
		ConfiguredDevice.STREAM_CODEC, VisualizationDataPayload::device,
		ResourceLocation.STREAM_CODEC, VisualizationDataPayload::sensorId,
		ResourceLocation.STREAM_CODEC, VisualizationDataPayload::vizId,
		IVisualizationData.STREAM_CODEC, VisualizationDataPayload::data,
		VisualizationDataPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(VisualizationDataPayload payload, IPayloadContext context) {
		var homeScreen = HomeScreen.get();
		if(homeScreen == null) {
			return;
		}

		if(homeScreen.selectedHome == null || !homeScreen.selectedHome.id().equals(payload.homeId())) {
			return;
		}

		homeScreen.setVisualizationData(payload.device().id(), payload.sensorId(), payload.vizId(), payload.data());
	}
}
