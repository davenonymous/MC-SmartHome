package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.content.blocks.base.HomeBlockEntity;
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
public record SetSelectedHomePayload(BlockPos homeEntityPos, UUID homeId) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetSelectedHomePayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetSelectedHomePayload::homeEntityPos,
		UUIDUtil.STREAM_CODEC, SetSelectedHomePayload::homeId,
		SetSelectedHomePayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetSelectedHomePayload payload, IPayloadContext context) {
		var player = context.player();
		var level = player.level();
		var blockEntity = level.getBlockEntity(payload.homeEntityPos());
		if(!(blockEntity instanceof HomeBlockEntity homeEntity)) {
			return;
		}
		if(!homeEntity.isOwner(player)) {
			return;
		}
		homeEntity.setHome(payload.homeId());
		homeEntity.setChanged();
	}
}
