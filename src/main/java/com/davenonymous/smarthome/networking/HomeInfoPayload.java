package com.davenonymous.smarthome.networking;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.data.HomeCore;
import com.davenonymous.smarthome.gui.HomeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HomeInfoPayload(HomeCore home) implements CustomPacketPayload {
	public static final Type<HomeInfoPayload> TYPE = new Type<>(SmartHome.resource("home_info"));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeInfoPayload> CODEC = StreamCodec.composite(
		HomeCore.STREAM_CODEC, HomeInfoPayload::home,
		HomeInfoPayload::new
	);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleOnClient(HomeInfoPayload payload, IPayloadContext context) {
		SmartHome.LOGGER.debug("Received home info packet for home {}", payload.home.name());
		var mc = Minecraft.getInstance();
		if(mc.screen instanceof HomeScreen homeScreen) {
			//homeScreen.updateHome(payload.home);
			return;
		}

		Minecraft.getInstance().pushGuiLayer(new HomeScreen());
	}
}
