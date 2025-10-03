package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.data.HomeZone;
import com.davenonymous.smarthome.data.WorldSavedHomes;
import com.davenonymous.smarthome.networking.HomeInfoPayload;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record SetZoneAABBPayload(UUID homeId, UUID zone, Direction dir, boolean move, boolean grow, boolean shrink) implements LibPacketPayload {

	public static SetZoneAABBPayload move(HomeZone zone, Direction dir) {
		return new SetZoneAABBPayload(zone.home().id(), zone.id(), dir, true, false, false);
	}

	public static SetZoneAABBPayload grow(HomeZone zone, Direction dir) {
		return new SetZoneAABBPayload(zone.home().id(), zone.id(), dir, false, true, false);
	}

	public static SetZoneAABBPayload shrink(HomeZone zone, Direction dir) {
		return new SetZoneAABBPayload(zone.home().id(), zone.id(), dir, false, false, true);
	}

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetZoneAABBPayload> CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, SetZoneAABBPayload::homeId,
		UUIDUtil.STREAM_CODEC, SetZoneAABBPayload::zone,
		Direction.STREAM_CODEC, SetZoneAABBPayload::dir,
		ByteBufCodecs.BOOL, SetZoneAABBPayload::move,
		ByteBufCodecs.BOOL, SetZoneAABBPayload::grow,
		ByteBufCodecs.BOOL, SetZoneAABBPayload::shrink,
		SetZoneAABBPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetZoneAABBPayload payload, IPayloadContext context) {
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

		var optZone = home.getZone(payload.zone());
		if(optZone.isEmpty()) {
			return;
		}

		if(!payload.move() && !payload.grow() && !payload.shrink()) {
			return;
		}

		var zone = optZone.get();
		if((payload.move() || payload.grow()) && !zone.canGrow(payload.dir())) {
			return;
		}
		if(payload.shrink() && !zone.canShrink(payload.dir())) {
			return;
		}

		if(payload.move()) {
			zone.setBounds(zone.getMovedBounds(payload.dir()));
		}
		if(payload.grow()) {
			zone.setBounds(zone.getExpandedBounds(payload.dir()));
		}
		if(payload.shrink()) {
			zone.setBounds(zone.getContractedBounds(payload.dir()));
		}

		homes.setDirty();
		context.enqueueWork(() -> {
			context.reply(new HomeInfoPayload(home, HomeWorldInfo.create(home.getHomeLevel(player.getServer()), home)));
		});

	}
}
