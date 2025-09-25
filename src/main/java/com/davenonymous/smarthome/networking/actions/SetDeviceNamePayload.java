package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.ConfiguredDevice;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.lib.DimPos;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Nameable;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SetDeviceNamePayload(UUID homeId, UUID zoneId, ConfiguredDevice device, String name) implements CustomPacketPayload {
	public static final Type<SetDeviceNamePayload> TYPE = new Type<>(SmartHome.resource("set_device_name"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetDeviceNamePayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetDeviceNamePayload::homeId,
		UUIDUtil.STREAM_CODEC, SetDeviceNamePayload::zoneId,
		ConfiguredDevice.STREAM_CODEC, SetDeviceNamePayload::device,
		ByteBufCodecs.STRING_UTF8, SetDeviceNamePayload::name,
		SetDeviceNamePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(SetDeviceNamePayload payload, IPayloadContext context) {
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
		zone.setDeviceName(payload.device(), payload.name());

		if(home.settings().renameBlocksToDeviceNames()) {
			ServerLevel homeLevel = home.getHomeLevel(level.getServer());
			if(homeLevel != null) {
				var blockPos = payload.device().pos();
				if(blockPos != null) {
					var blockEntity = homeLevel.getBlockEntity(blockPos);
					if(blockEntity != null) {
						var tag = blockEntity.getUpdateTag(homeLevel.registryAccess());
						tag.putString("CustomName", Component.Serializer.toJson(Component.literal(payload.name()), homeLevel.registryAccess()));
						blockEntity.loadCustomOnly(tag, homeLevel.registryAccess());
						blockEntity.setChanged();
					}
				}
			}
		}

		homes.setDirty();
		context.reply(HomeInfoPayload.get(player.getServer(), home));
	}
}
