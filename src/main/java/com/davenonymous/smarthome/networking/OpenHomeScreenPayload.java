package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.FoundDevice;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record OpenHomeScreenPayload(BlockPos pos, UUID selectedHome, List<HomeCore> homes, List<FoundDevice> newDevices) implements CustomPacketPayload {
	public static final Type<OpenHomeScreenPayload> TYPE = new Type<>(SmartHome.resource("open_home_screen"));

	public static final StreamCodec<RegistryFriendlyByteBuf, OpenHomeScreenPayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, OpenHomeScreenPayload::pos,
		UUIDUtil.STREAM_CODEC, OpenHomeScreenPayload::selectedHome,
		HomeCore.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenHomeScreenPayload::homes,
		FoundDevice.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenHomeScreenPayload::newDevices,
		OpenHomeScreenPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(OpenHomeScreenPayload payload, IPayloadContext context) {
		ClientCache.INSTANCE.ownedHomes = payload.homes();
		SmartHome.LOGGER.debug("Received open home screen packet for {} homes", payload.homes.size());

		var mc = Minecraft.getInstance();
		if(mc.screen instanceof HomeScreen homeScreen) {
			homeScreen.newDevices = new ArrayList<>(payload.newDevices);
			homeScreen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
			return;
		}

		Minecraft.getInstance().setScreen(new HomeScreen(payload.pos(), payload.selectedHome(), payload.homes(), payload.newDevices()));
	}
}
