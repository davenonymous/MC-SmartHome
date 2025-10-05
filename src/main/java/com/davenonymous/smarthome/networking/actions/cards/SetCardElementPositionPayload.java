package com.davenonymous.smarthome.networking.actions.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.dynamic.ModCardElements;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.davenonymous.smarthome.util.MoreCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record SetCardElementPositionPayload(UUID homeId, UUID cardId, UUID elementId, Vec2 pos) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetCardElementPositionPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetCardElementPositionPayload::homeId,
		UUIDUtil.STREAM_CODEC, SetCardElementPositionPayload::cardId,
		UUIDUtil.STREAM_CODEC, SetCardElementPositionPayload::elementId,
		MoreCodecs.VEC2_STREAM_CODEC, SetCardElementPositionPayload::pos,
		SetCardElementPositionPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetCardElementPositionPayload payload, IPayloadContext context) {
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
		var newCard = card.setElementPosition(payload.elementId(), payload.pos());
		home.addCard(newCard);
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
