package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.blocks.dashboard.DashboardBlockEntity;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

@Packet
public record SetSelectedHomePayload(BlockPos dashboardPos, UUID homeId) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetSelectedHomePayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetSelectedHomePayload::dashboardPos,
		UUIDUtil.STREAM_CODEC, SetSelectedHomePayload::homeId,
		SetSelectedHomePayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetSelectedHomePayload payload, IPayloadContext context) {
		var player = context.player();
		var level = player.level();
		var blockEntity = level.getBlockEntity(payload.dashboardPos());
		if(!(blockEntity instanceof DashboardBlockEntity dashboard)) {
			return;
		}
		if(!dashboard.isOwner(player)) {
			return;
		}
		dashboard.setHome(payload.homeId());
		dashboard.setChanged();
	}
}
