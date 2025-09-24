package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.WorldWatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HomeInfoPayload(HomeCore home, HomeWorldInfo worldInfo) implements CustomPacketPayload {

	public static HomeInfoPayload get(MinecraftServer server, HomeCore home) {
		var foundDevices = WorldWatcher.searchForDevices(server, home);
		home.setFoundDevices(foundDevices);
		return new HomeInfoPayload(home, HomeWorldInfo.create(home.getHomeLevel(server), home));
	}

	public static final Type<HomeInfoPayload> TYPE = new Type<>(SmartHome.resource("home_info"));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeInfoPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC, HomeInfoPayload::home,
		HomeWorldInfo.STREAM_CODEC, HomeInfoPayload::worldInfo,
		HomeInfoPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(HomeInfoPayload payload, IPayloadContext context) {
		var mc = Minecraft.getInstance();
		if(mc.screen instanceof HomeScreen homeScreen) {
			var home = payload.home();
			homeScreen.ownedHomes.removeIf(h -> h.id().equals(home.id()));
			homeScreen.ownedHomes.add(home);

			if(homeScreen.selectedHome != null && homeScreen.selectedHome.id().equals(home.id())) {
				homeScreen.selectedHome = home;
			}
			homeScreen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
		}
	}
}
