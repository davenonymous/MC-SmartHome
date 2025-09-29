package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import com.davenonymous.smarthome.lib.gui.event.GuiDataUpdatedEvent;
import com.davenonymous.smarthome.setup.dynamic.annotations.Packet;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketCodec;
import com.davenonymous.smarthome.setup.dynamic.annotations.PacketHandler;
import com.davenonymous.smarthome.setup.dynamic.base.LibPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

@Packet
public record HomeInfoListPayload(List<HomeCore> homes) implements LibPacketPayload {

	@PacketCodec
	public static final StreamCodec<RegistryFriendlyByteBuf, HomeInfoListPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeInfoListPayload::homes,
		HomeInfoListPayload::new
	);

	@PacketHandler(PacketHandler.Receiver.Client)
	public static void handleOnClient(HomeInfoListPayload payload, IPayloadContext context) {
		ClientCache.INSTANCE.ownedHomes = payload.homes();

		var mc = Minecraft.getInstance();
		if(mc.screen instanceof HomeScreen homeScreen) {
			homeScreen.getOrCreateGui().fireEvent(new GuiDataUpdatedEvent());
		}
	}
}
