package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Packet
public record TestPacketDataPayload(String message) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, TestPacketDataPayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, TestPacketDataPayload::message,
		TestPacketDataPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(TestPacketDataPayload payload, IPayloadContext context) {
		LocalPlayer player = (LocalPlayer) context.player();
		player.displayClientMessage(Component.literal(payload.message()), true);
	}
}
