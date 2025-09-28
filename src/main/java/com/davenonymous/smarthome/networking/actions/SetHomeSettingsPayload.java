package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeSettings;
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

public record SetHomeSettingsPayload(UUID homeId, HomeSettings settings)  implements CustomPacketPayload {
	public static final Type<SetHomeSettingsPayload> TYPE = new Type<>(SmartHome.resource("set_home_settings"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetHomeSettingsPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetHomeSettingsPayload::homeId,
		HomeSettings.STREAM_CODEC, SetHomeSettingsPayload::settings,
		SetHomeSettingsPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(SetHomeSettingsPayload payload, IPayloadContext context) {
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

		home.setSettings(payload.settings());
		homes.setDirty();

		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
