package com.davenonymous.smarthome.sensor;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.api.SensorData;
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
public class SensorDataCodecRegistry {
	public static final ResourceKey<Registry<MapCodec<? extends SensorData>>> SENSOR_DATA_SERIALIZERS_KEY = ResourceKey.createRegistryKey(SmartHome.resource("sensor_data_serializers"));
	public static final Registry<MapCodec<? extends SensorData>> SENSOR_DATA_SERIALIZERS = new RegistryBuilder<>(SensorDataCodecRegistry.SENSOR_DATA_SERIALIZERS_KEY).create();
	public static final DeferredRegister<MapCodec<? extends SensorData>> DEFERRED_SENSOR_DATA = DeferredRegister.create(SensorDataCodecRegistry.SENSOR_DATA_SERIALIZERS, SmartHome.MODID);


	public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends SensorData>>> SENSOR_DATA_DISPATCHER_KEY = ResourceKey.createRegistryKey(SmartHome.resource("sensor_data_dispatcher"));
	public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends SensorData>> SENSOR_DATA_DISPATCHER = new RegistryBuilder<>(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER_KEY).sync(true).create();
	public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends SensorData>> DEFERRED_SENSOR_DATA_DISPATCHER = DeferredRegister.create(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER, SmartHome.MODID);

	static {
		DEFERRED_SENSOR_DATA.register("energy_storage", () -> EnergyStorageData.CODEC);
		DEFERRED_SENSOR_DATA_DISPATCHER.register("energy_storage", () -> EnergyStorageData.STREAM_CODEC);

		DEFERRED_SENSOR_DATA.register("redstone_powered", () -> RedstonePoweredData.CODEC);
		DEFERRED_SENSOR_DATA_DISPATCHER.register("redstone_powered", () -> RedstonePoweredData.STREAM_CODEC);

		DEFERRED_SENSOR_DATA.register("occupancy", () -> OccupancyData.CODEC);
		DEFERRED_SENSOR_DATA_DISPATCHER.register("occupancy", () -> OccupancyData.STREAM_CODEC);


	}


	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(SensorDataCodecRegistry.SENSOR_DATA_SERIALIZERS);
		event.register(SensorDataCodecRegistry.SENSOR_DATA_DISPATCHER);
	}

}
