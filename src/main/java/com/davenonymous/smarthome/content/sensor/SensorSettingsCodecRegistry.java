package com.davenonymous.smarthome.content.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.content.sensor.settings.SensorSettings;
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
public class SensorSettingsCodecRegistry {
	public static final ResourceKey<Registry<MapCodec<? extends SensorSettings>>> SENSOR_SETTINGS_SERIALIZERS_KEY = ResourceKey.createRegistryKey(SmartHome.resource("sensor_settings_serializers"));
	public static final Registry<MapCodec<? extends SensorSettings>> SENSOR_SETTINGS_SERIALIZERS = new RegistryBuilder<>(SensorSettingsCodecRegistry.SENSOR_SETTINGS_SERIALIZERS_KEY).create();
	public static final DeferredRegister<MapCodec<? extends SensorSettings>> DEFERRED_SENSOR_SETTINGS = DeferredRegister.create(SensorSettingsCodecRegistry.SENSOR_SETTINGS_SERIALIZERS, SmartHome.MODID);


	public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends SensorSettings>>> SENSOR_SETTINGS_DISPATCHER_KEY = ResourceKey.createRegistryKey(SmartHome.resource("sensor_settings_dispatcher"));
	public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends SensorSettings>> SENSOR_SETTINGS_DISPATCHER = new RegistryBuilder<>(SensorSettingsCodecRegistry.SENSOR_SETTINGS_DISPATCHER_KEY).sync(true).create();
	public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends SensorSettings>> DEFERRED_SENSOR_SETTINGS_DISPATCHER = DeferredRegister.create(SensorSettingsCodecRegistry.SENSOR_SETTINGS_DISPATCHER, SmartHome.MODID);

	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(SensorSettingsCodecRegistry.SENSOR_SETTINGS_SERIALIZERS);
		event.register(SensorSettingsCodecRegistry.SENSOR_SETTINGS_DISPATCHER);
	}

}
