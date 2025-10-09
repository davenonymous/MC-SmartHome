package com.davenonymous.smarthome.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigs {
	public static final ModConfigSpec SERVER_SPEC;
	public static final ModConfigSpec CLIENT_SPEC;

	public static final ServerConfig ServerConfig;
	public static final ClientConfig ClientConfig;

	static {
		ModConfigSpec.Builder commonBuilder = new ModConfigSpec.Builder();
		ServerConfig = new ServerConfig(commonBuilder);
		SERVER_SPEC = commonBuilder.build();


		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		ClientConfig = new ClientConfig(builder);
		CLIENT_SPEC = builder.build();

	}

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		if(event.getConfig().getSpec() == SERVER_SPEC) {
			ServerConfig.load();
		} else if(event.getConfig().getSpec() == CLIENT_SPEC) {
			ClientConfig.load();
		}
	}
}
