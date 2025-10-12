package com.davenonymous.smarthome.networking.actions.cards;

import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record RemoveCardElementPayload(UUID homeId, UUID cardId, UUID elementId) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, RemoveCardElementPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, RemoveCardElementPayload::homeId,
		UUIDUtil.STREAM_CODEC, RemoveCardElementPayload::cardId,
		UUIDUtil.STREAM_CODEC, RemoveCardElementPayload::elementId,
		RemoveCardElementPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(RemoveCardElementPayload payload, IPayloadContext context) {
		var player = context.player();

		var homes = WorldSavedHomes.get((ServerLevel) player.level());
		var optHome = homes.getHome(payload.homeId());
		if(optHome.isEmpty()) {
			return;
		}

		var home = optHome.get();
		if(!home.owner().equals(player.getUUID())) {
			return;
		}

		var optCard = home.getCard(payload.cardId());
		if(optCard.isEmpty()) {
			return;
		}

		var card = optCard.get();
		var newCard = card.withoutElement(payload.elementId());
		home.addCard(newCard);
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
