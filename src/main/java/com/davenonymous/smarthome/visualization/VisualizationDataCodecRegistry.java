package com.davenonymous.smarthome.visualization;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.visualization.IVisualizationData;
import com.davenonymous.smarthome.visualization.gauge.GaugeVizData;
import com.davenonymous.smarthome.visualization.line.LineVizData;
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
public class VisualizationDataCodecRegistry {

	public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends IVisualizationData>>> VIZ_DATA_DISPATCHER_KEY = ResourceKey.createRegistryKey(SmartHome.resource("visualization_data_dispatcher"));
	public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends IVisualizationData>> VIZ_DATA_DISPATCHER = new RegistryBuilder<>(VisualizationDataCodecRegistry.VIZ_DATA_DISPATCHER_KEY).sync(true).create();
	public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends IVisualizationData>> DEFERRED_VIZ_DATA_DISPATCHER = DeferredRegister.create(VisualizationDataCodecRegistry.VIZ_DATA_DISPATCHER, SmartHome.MODID);

	static {
		DEFERRED_VIZ_DATA_DISPATCHER.register("gauge", () -> GaugeVizData.STREAM_CODEC);
		DEFERRED_VIZ_DATA_DISPATCHER.register("line", () -> LineVizData.STREAM_CODEC);
	}

	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(VisualizationDataCodecRegistry.VIZ_DATA_DISPATCHER);
	}

}
