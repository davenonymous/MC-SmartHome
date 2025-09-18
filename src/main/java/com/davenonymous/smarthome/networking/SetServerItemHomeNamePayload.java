package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.items.ServerDataComponent;
import com.davenonymous.smarthome.items.ServerItem;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.setup.content.ModDataComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetServerItemHomeNamePayload(String name) implements CustomPacketPayload {
	public static final Type<SetServerItemHomeNamePayload> TYPE = new Type<>(SmartHome.resource("set_server_item_home_name"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SetServerItemHomeNamePayload> CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, SetServerItemHomeNamePayload::name,
		SetServerItemHomeNamePayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnServer(SetServerItemHomeNamePayload payload, IPayloadContext context) {
		var player = context.player();
		var serverItem = player.getItemInHand(InteractionHand.MAIN_HAND);
		if(!(serverItem.getItem() instanceof ServerItem)) {
			return;
		}

		if(!serverItem.has(ModDataComponents.SERVER_DATA_COMPONENT)) {
			serverItem.set(ModDataComponents.SERVER_DATA_COMPONENT, new ServerDataComponent(player));
		} else {
			ServerDataComponent data = serverItem.get(ModDataComponents.SERVER_DATA_COMPONENT);
			serverItem.set(ModDataComponents.SERVER_DATA_COMPONENT, data.withName(payload.name()));
		}

		player.setItemInHand(InteractionHand.MAIN_HAND, serverItem.copy());
	}
}
