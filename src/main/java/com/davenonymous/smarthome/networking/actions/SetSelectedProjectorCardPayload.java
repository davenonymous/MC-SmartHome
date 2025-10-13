package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.content.blocks.projector.ProjectorBlockEntity;
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
public record SetSelectedProjectorCardPayload(BlockPos projectorPos, UUID cardId) implements LibPacketPayload {
	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, SetSelectedProjectorCardPayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetSelectedProjectorCardPayload::projectorPos,
		UUIDUtil.STREAM_CODEC, SetSelectedProjectorCardPayload::cardId,
		SetSelectedProjectorCardPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Server)
	public static void handleOnServer(SetSelectedProjectorCardPayload payload, IPayloadContext context) {
		var player = context.player();
		var level = player.level();
		var blockEntity = level.getBlockEntity(payload.projectorPos());
		if(!(blockEntity instanceof ProjectorBlockEntity projectorEntity)) {
			return;
		}
		if(!projectorEntity.isOwner(player)) {
			return;
		}
		projectorEntity.setSelectedCard(payload.cardId());
		projectorEntity.setChanged();
		projectorEntity.notifyClients(false);
	}
}
