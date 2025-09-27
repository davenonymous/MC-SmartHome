package com.davenonymous.smarthome.visualization;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualizationSettings;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizSettings;
import com.davenonymous.smarthome.visualization.line.LineVizSettings;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = SmartHome.MODID)
public class VisualizationSettingsCodecRegistry {
	public static final ResourceKey<Registry<MapCodec<? extends IVisualizationSettings>>> VIZ_SETTINGS_SERIALIZERS_KEY = ResourceKey.createRegistryKey(SmartHome.resource("visualization_settings_serializers"));
	public static final Registry<MapCodec<? extends IVisualizationSettings>> VIZ_SETTINGS_SERIALIZERS = new RegistryBuilder<>(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_SERIALIZERS_KEY).create();
	public static final DeferredRegister<MapCodec<? extends IVisualizationSettings>> DEFERRED_VIZ_SETTINGS = DeferredRegister.create(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_SERIALIZERS, SmartHome.MODID);


	public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends IVisualizationSettings>>> VIZ_SETTINGS_DISPATCHER_KEY = ResourceKey.createRegistryKey(SmartHome.resource("visualization_settings_dispatcher"));
	public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends IVisualizationSettings>> VIZ_SETTINGS_DISPATCHER = new RegistryBuilder<>(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_DISPATCHER_KEY).sync(true).create();
	public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends IVisualizationSettings>> DEFERRED_VIZ_SETTINGS_DISPATCHER = DeferredRegister.create(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_DISPATCHER, SmartHome.MODID);

	static {
		DEFERRED_VIZ_SETTINGS.register("gauge", () -> GaugeVizSettings.CODEC);
		DEFERRED_VIZ_SETTINGS_DISPATCHER.register("gauge", () -> GaugeVizSettings.STREAM_CODEC);

		DEFERRED_VIZ_SETTINGS.register("line_chart", () -> LineVizSettings.CODEC);
		DEFERRED_VIZ_SETTINGS_DISPATCHER.register("line_chart", () -> LineVizSettings.STREAM_CODEC);
	}


	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_SERIALIZERS);
		event.register(VisualizationSettingsCodecRegistry.VIZ_SETTINGS_DISPATCHER);
	}

}
