package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.networking.data.HomeWorldInfo;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import com.davenonymous.smarthome.watcher.WorldWatcherUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.UUID;

@Packet
public record OpenHomeScreenPayload(BlockPos pos, UUID selectedHome, List<HomeCore> homes, HomeWorldInfo worldInfo) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, OpenHomeScreenPayload> CODEC = StreamCodec.composite(
		BlockPos.STREAM_CODEC, OpenHomeScreenPayload::pos,
		UUIDUtil.STREAM_CODEC, OpenHomeScreenPayload::selectedHome,
		HomeCore.STREAM_CODEC.apply(ByteBufCodecs.list()), OpenHomeScreenPayload::homes,
		HomeWorldInfo.STREAM_CODEC, OpenHomeScreenPayload::worldInfo,
		OpenHomeScreenPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(OpenHomeScreenPayload payload, IPayloadContext context) {
		var home = payload.homes().stream().filter(h -> h.id().equals(payload.selectedHome())).findFirst().orElse(null);
		if(home == null) {
			return;
		}
		ClientCache.addHomeInfo(home, payload.worldInfo());

		var mc = Minecraft.getInstance();
		if(mc.screen instanceof HomeScreen homeScreen) {
			homeScreen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
			return;
		}

		WorldWatcherUtil.autoIgnoreGenericOnlyDevices(home);

		Minecraft.getInstance().setScreen(new HomeScreen(payload.pos(), payload.selectedHome(), payload.homes(), payload.worldInfo()));
	}
}
