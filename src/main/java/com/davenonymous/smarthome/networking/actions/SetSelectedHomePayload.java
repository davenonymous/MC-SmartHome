package com.davenonymous.smarthome.networking.actions;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.blocks.dashboard.DashboardBlockEntity;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record SetSelectedHomePayload(BlockPos dashboardPos, UUID homeId) implements CustomPacketPayload {
	public static final Type<SetSelectedHomePayload> TYPE = new Type<>(SmartHome.resource("set_dashboard_selected_home"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetSelectedHomePayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, SetSelectedHomePayload::dashboardPos,
		UUIDUtil.STREAM_CODEC, SetSelectedHomePayload::homeId,
		SetSelectedHomePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

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
