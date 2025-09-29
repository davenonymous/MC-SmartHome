package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Packet
public record SetServerItemHomeNamePayload(String name) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetServerItemHomeNamePayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SetServerItemHomeNamePayload::name,
		SetServerItemHomeNamePayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetServerItemHomeNamePayload payload, IPayloadContext context) {
		var player = context.player();
		var serverItem = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(serverItem.getItem() instanceof ServerItem)) {
			return;
		}

		if(!serverItem.has(ModDataComponents.SERVER_DATA_COMPONENT)) {
			serverItem.set(ModDataComponents.SERVER_DATA_COMPONENT, new ServerDataComponent(player));
		} else {
			ServerDataComponent data = serverItem.get(ModDataComponents.SERVER_DATA_COMPONENT);
			serverItem.set(ModDataComponents.SERVER_DATA_COMPONENT, data.withName(payload.name()));
		}

		player.setItemInHand(InteractionHand.MAIN_HAND, serverItem.copy());
	}
}
