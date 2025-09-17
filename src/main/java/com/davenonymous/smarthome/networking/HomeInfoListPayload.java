package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record HomeInfoListPayload(List<HomeCore> homes) implements CustomPacketPayload {
	public static final Type<HomeInfoListPayload> TYPE = new Type<>(SmartHome.resource("home_info_list"));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeInfoListPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeInfoListPayload::homes,
		HomeInfoListPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(HomeInfoListPayload payload, IPayloadContext context) {
		ClientCache.INSTANCE.ownedHomes = payload.homes();

		var mc = Minecraft.getInstance();
		if(mc.screen instanceof HomeScreen homeScreen) {
			homeScreen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
		}
	}
}
