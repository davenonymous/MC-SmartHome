package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.DashboardScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@Packet
public record HomeInfoPayload(HomeCore home, HomeWorldInfo worldInfo) implements LibPacketPayload {

	public static HomeInfoPayload get(MinecraftServer server, HomeCore home) {
		WorldWatcherUtil.updateDevicesInHome(server, home);
		return new HomeInfoPayload(home, HomeWorldInfo.create(home.getHomeLevel(server), home));
	}

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, HomeInfoPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC, HomeInfoPayload::home,
		HomeWorldInfo.STREAM_CODEC, HomeInfoPayload::worldInfo,
		HomeInfoPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(HomeInfoPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			ClientCache.addHomeInfo(payload.home(), payload.worldInfo());

			var mc = Minecraft.getInstance();
			if(mc.screen instanceof DashboardScreen dashboardScreen) {
				var home = payload.home();

				dashboardScreen.getMenu().ownedHomes.removeIf(h -> h.id().equals(home.id()));
				dashboardScreen.getMenu().ownedHomes.add(home);

				if(dashboardScreen.selectedHome != null && dashboardScreen.selectedHome.id().equals(home.id())) {
					dashboardScreen.selectedHome = home;
				}


				dashboardScreen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
			}
		});
	}
}
