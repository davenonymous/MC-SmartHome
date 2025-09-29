package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.lib.DimPos;
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
public record SetZoneNamePayload(DimPos rackPos, UUID homeId, UUID zoneId, String name) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetZoneNamePayload> CODEC = StreamCodec.composite(
		DimPos.STREAM_CODEC, SetZoneNamePayload::rackPos,
		UUIDUtil.STREAM_CODEC, SetZoneNamePayload::homeId,
		UUIDUtil.STREAM_CODEC, SetZoneNamePayload::zoneId,
		ByteBufCodecs.STRING_UTF8, SetZoneNamePayload::name,
		SetZoneNamePayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetZoneNamePayload payload, IPayloadContext context) {
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

		var optZone = home.getZone(payload.zoneId());
		if(optZone.isEmpty()) {
			return;
		}

		var zone = optZone.get();
		zone.setName(payload.name());
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
