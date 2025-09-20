package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.dashboard.DashboardBlockEntity;
import com.davenonymous.smarthome.blocks.minirack.MiniRackBlockEntity;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.lib.DimPos;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SetZoneNamePayload(DimPos rackPos, UUID homeId, UUID zoneId, String name) implements CustomPacketPayload {
	public static final Type<SetZoneNamePayload> TYPE = new Type<>(SmartHome.resource("set_zone_name"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetZoneNamePayload> CODEC = StreamCodec.composite(
		DimPos.STREAM_CODEC, SetZoneNamePayload::rackPos,
		UUIDUtil.STREAM_CODEC, SetZoneNamePayload::homeId,
		UUIDUtil.STREAM_CODEC, SetZoneNamePayload::zoneId,
		ByteBufCodecs.STRING_UTF8, SetZoneNamePayload::name,
		SetZoneNamePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

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

		context.reply(new HomeInfoPayload(home));
	}
}
