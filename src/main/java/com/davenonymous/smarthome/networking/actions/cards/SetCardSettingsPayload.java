package com.davenonymous.smarthome.networking.actions.cards;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
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
public record SetCardSettingsPayload(UUID homeId, UUID cardId, String label, ResourceLocation icon, Vec2 size) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetCardSettingsPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetCardSettingsPayload::homeId,
		UUIDUtil.STREAM_CODEC, SetCardSettingsPayload::cardId,
		ByteBufCodecs.STRING_UTF8, SetCardSettingsPayload::label,
		ResourceLocation.STREAM_CODEC, SetCardSettingsPayload::icon,
		MoreCodecs.VEC2_STREAM_CODEC, SetCardSettingsPayload::size,
		SetCardSettingsPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetCardSettingsPayload payload, IPayloadContext context) {
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

		var card = optCard.get()
			.withSize(payload.size())
			.withLabel(payload.label())
			.withIcon(payload.icon());

		home.addCard(card);
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
