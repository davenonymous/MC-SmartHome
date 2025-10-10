package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

@Packet
public record OpenHomeScreenPayload(List<HomeCore> homes, HomeWorldInfo worldInfo) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenHomeScreenPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenHomeScreenPayload::homes,
		HomeWorldInfo.STREAM_CODEC, OpenHomeScreenPayload::worldInfo,
		OpenHomeScreenPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(OpenHomeScreenPayload payload, IPayloadContext context) {

	}
}
