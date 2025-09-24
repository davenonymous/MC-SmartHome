package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.IgnoredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record IgnoreDevicePayload(UUID homeId, UUID zone, IgnoredDevice device)  implements CustomPacketPayload {
	public static final Type<IgnoreDevicePayload> TYPE = new Type<>(SmartHome.resource("ignore_device"));

	public static final StreamCodec<RegistryFriendlyByteBuf, IgnoreDevicePayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, IgnoreDevicePayload::homeId,
		UUIDUtil.STREAM_CODEC, IgnoreDevicePayload::zone,
		IgnoredDevice.STREAM_CODEC, IgnoreDevicePayload::device,
		IgnoreDevicePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(IgnoreDevicePayload payload, IPayloadContext context) {
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

		var optZone = home.getZone(payload.zone());
		if(optZone.isEmpty()) {
			return;
		}

		var zone = optZone.get();
		zone.addIgnoredDevice(payload.device());
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
