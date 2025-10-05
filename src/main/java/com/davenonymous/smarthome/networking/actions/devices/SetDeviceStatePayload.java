package com.davenonymous.smarthome.networking.actions.devices;

import com.davenonymous.smarthome.data.ConfiguredDevice;
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
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record SetDeviceStatePayload(UUID homeId, UUID zoneId, ConfiguredDevice device, boolean enabled) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetDeviceStatePayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetDeviceStatePayload::homeId,
		UUIDUtil.STREAM_CODEC, SetDeviceStatePayload::zoneId,
		ConfiguredDevice.STREAM_CODEC, SetDeviceStatePayload::device,
		ByteBufCodecs.BOOL, SetDeviceStatePayload::enabled,
		SetDeviceStatePayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetDeviceStatePayload payload, IPayloadContext context) {
		ServerPlayer player = (ServerPlayer) context.player();
		ServerLevel level = (ServerLevel) player.level();

		var homes = WorldSavedHomes.get(level);
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
		zone.setDeviceState(payload.device(), payload.enabled());

		homes.setDirty();
		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
