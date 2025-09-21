package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record MarkZoneAsDeletedPayload(UUID homeId, UUID zone, boolean restore)  implements CustomPacketPayload {
	public static final Type<MarkZoneAsDeletedPayload> TYPE = new Type<>(SmartHome.resource("mark_zone_as_deleted"));

	public static final StreamCodec<RegistryFriendlyByteBuf, MarkZoneAsDeletedPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, MarkZoneAsDeletedPayload::homeId,
		UUIDUtil.STREAM_CODEC, MarkZoneAsDeletedPayload::zone,
		ByteBufCodecs.BOOL, MarkZoneAsDeletedPayload::restore,
		MarkZoneAsDeletedPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(MarkZoneAsDeletedPayload payload, IPayloadContext context) {
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
		zone.setDeleted(!payload.restore());
		homes.setDirty();

		SmartHome.LOGGER.info("Received request to delete zone '{}' from home {}", payload.zone, payload.homeId);
		context.reply(new HomeInfoPayload(home));
	}
}
