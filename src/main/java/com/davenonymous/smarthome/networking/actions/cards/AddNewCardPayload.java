package com.davenonymous.smarthome.networking.actions.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCard;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record AddNewCardPayload(UUID homeId, String name) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, AddNewCardPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, AddNewCardPayload::homeId,
		ByteBufCodecs.STRING_UTF8, AddNewCardPayload::name,
		AddNewCardPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(AddNewCardPayload payload, IPayloadContext context) {
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

		var card = new HomeCard(payload.name());
		home.addCard(card);
		homes.setDirty();

		SmartHome.LOGGER.info("Received request to add new card '{}' to home {}", payload.name, payload.homeId);
		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
