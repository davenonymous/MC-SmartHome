package com.davenonymous.smarthome.networking.actions.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.cards.HomeCardElement;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record SetCardElementSettingsPayload(UUID homeId, UUID cardId, UUID elementId, HomeCardElement<?> element) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetCardElementSettingsPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetCardElementSettingsPayload::homeId,
		UUIDUtil.STREAM_CODEC, SetCardElementSettingsPayload::cardId,
		UUIDUtil.STREAM_CODEC, SetCardElementSettingsPayload::elementId,
		HomeCardElement.STREAM_CODEC, SetCardElementSettingsPayload::element,
		SetCardElementSettingsPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetCardElementSettingsPayload payload, IPayloadContext context) {
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
		Pair<Vec2, HomeCardElement<?>> oldSettings = card.elements().get(payload.elementId());
		if(oldSettings == null) {
			return;
		}

		var newCard = card.withElement(oldSettings.getFirst(), payload.element());
		home.addCard(newCard);
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
