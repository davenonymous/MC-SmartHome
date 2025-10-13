package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

@Packet
public record OpenProjectorScreenPayload(List<HomeCore> homes, UUID homeId, UUID cardId) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenProjectorScreenPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenProjectorScreenPayload::homes,
		UUIDUtil.STREAM_CODEC, OpenProjectorScreenPayload::homeId,
		UUIDUtil.STREAM_CODEC, OpenProjectorScreenPayload::cardId,
		OpenProjectorScreenPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(OpenProjectorScreenPayload payload, IPayloadContext context) {

	}
}
