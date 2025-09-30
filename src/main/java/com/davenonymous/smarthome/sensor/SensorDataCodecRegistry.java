package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.ISensorData;
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
public class SensorDataCodecRegistry {

	public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends ISensorData>>> SENSOR_DATA_DISPATCHER_KEY = ResourceKey.createRegistryKey(SmartHome.resource("sensor_data_dispatcher"));
	public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends ISensorData>> SENSOR_DATA_DISPATCHER = new RegistryBuilder<>(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER_KEY).sync(true).create();
	public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends ISensorData>> DEFERRED_SENSOR_DATA_DISPATCHER = DeferredRegister.create(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER, SmartHome.MODID);

	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER);
	}

}
