package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.sensor.SensorSettings;
import com.davenonymous.smarthome.sensor.energy.EnergyStorageSettings;
import com.davenonymous.smarthome.sensor.fluid.FluidStorageSettings;
import com.davenonymous.smarthome.sensor.occupancy.OccupancySettings;
import com.davenonymous.smarthome.sensor.redstone.RedstonePoweredSettings;
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

	static {
		DEFERRED_SENSOR_SETTINGS.register("energy_storage", () -> EnergyStorageSettings.CODEC);
		DEFERRED_SENSOR_SETTINGS_DISPATCHER.register("energy_storage", () -> EnergyStorageSettings.STREAM_CODEC);

		DEFERRED_SENSOR_SETTINGS.register("redstone_powered", () -> RedstonePoweredSettings.CODEC);
		DEFERRED_SENSOR_SETTINGS_DISPATCHER.register("redstone_powered", () -> RedstonePoweredSettings.STREAM_CODEC);

		DEFERRED_SENSOR_SETTINGS.register("occupancy", () -> OccupancySettings.CODEC);
		DEFERRED_SENSOR_SETTINGS_DISPATCHER.register("occupancy", () -> OccupancySettings.STREAM_CODEC);

		DEFERRED_SENSOR_SETTINGS.register("fluid_storage", () -> FluidStorageSettings.CODEC);
		DEFERRED_SENSOR_SETTINGS_DISPATCHER.register("fluid_storage", () -> FluidStorageSettings.STREAM_CODEC);

	}


	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(SensorSettingsCodecRegistry.SENSOR_SETTINGS_SERIALIZERS);
		event.register(SensorSettingsCodecRegistry.SENSOR_SETTINGS_DISPATCHER);
	}

}
