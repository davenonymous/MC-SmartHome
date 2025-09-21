package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.util.MoreCodecs;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record AddNewZonePayload(UUID homeId, AABB area, String name)  implements CustomPacketPayload {
	public static final Type<AddNewZonePayload> TYPE = new Type<>(SmartHome.resource("add_new_zone"));

	public static final StreamCodec<RegistryFriendlyByteBuf, AddNewZonePayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, AddNewZonePayload::homeId,
		MoreCodecs.AABB_STREAM_CODEC, AddNewZonePayload::area,
		ByteBufCodecs.STRING_UTF8, AddNewZonePayload::name,
		AddNewZonePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(AddNewZonePayload payload, IPayloadContext context) {
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

		var newZone = new HomeZone(payload.name(), payload.area());
		home.addZone(newZone);
		homes.setDirty();

		SmartHome.LOGGER.info("Received request to add new zone '{}' to home {}", payload.name, payload.homeId);
		context.reply(new HomeInfoPayload(home));
	}
}
