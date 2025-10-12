package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.data.TimeRangeEnum;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record SetTimeRangePayload(UUID homeId, TimeRangeEnum timeRange) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetTimeRangePayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetTimeRangePayload::homeId,
		TimeRangeEnum.STREAM_CODEC, SetTimeRangePayload::timeRange,
		SetTimeRangePayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetTimeRangePayload payload, IPayloadContext context) {
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

		home.setTimeRange(payload.timeRange());
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
