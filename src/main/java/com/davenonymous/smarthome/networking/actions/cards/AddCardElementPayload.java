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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record AddCardElementPayload(UUID homeId, UUID cardId, ResourceLocation elementId) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, AddCardElementPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, AddCardElementPayload::homeId,
		UUIDUtil.STREAM_CODEC, AddCardElementPayload::cardId,
		ResourceLocation.STREAM_CODEC, AddCardElementPayload::elementId,
		AddCardElementPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(AddCardElementPayload payload, IPayloadContext context) {
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

		HomeCardElement<?> element = ModCardElements.createDefault(payload.elementId());
		Vec2 center = new Vec2(card.width() / 2f, card.height() / 2f);
		var newCard = card.withElement(center, element);
		home.addCard(newCard);
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
