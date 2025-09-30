package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.api.sensor.ISensorData;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.UUID;

@Packet
public record VisualizationDataPayload(UUID homeId, UUID zoneId, ConfiguredDevice device, ResourceLocation sensorId, ResourceLocation vizId, LinkedHashMap<Pair<Instant, Long>, ISensorData> data) implements LibPacketPayload {

	public static final StreamCodec<RegistryFriendlyByteBuf, Pair<Instant, Long>> PAIR_CODEC = StreamCodec.composite(
		ByteBufCodecs.VAR_LONG.map(Instant::ofEpochMilli, Instant::toEpochMilli), Pair::getFirst,
		ByteBufCodecs.VAR_LONG, Pair::getSecond,
		Pair::new
	);

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, VisualizationDataPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, VisualizationDataPayload::homeId,
		UUIDUtil.STREAM_CODEC, VisualizationDataPayload::zoneId,
		ConfiguredDevice.STREAM_CODEC, VisualizationDataPayload::device,
		ResourceLocation.STREAM_CODEC, VisualizationDataPayload::sensorId,
		ResourceLocation.STREAM_CODEC, VisualizationDataPayload::vizId,
		ByteBufCodecs.map(LinkedHashMap::new, PAIR_CODEC, ISensorData.STREAM_CODEC), VisualizationDataPayload::data,
		VisualizationDataPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
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
