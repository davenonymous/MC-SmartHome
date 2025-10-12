package com.davenonymous.smarthome.content.cards;

import com.davenonymous.smarthome.SmartHome;
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
public class HomeCardElementCodecRegistry {
	public static final ResourceKey<Registry<MapCodec<? extends HomeCardElement<?>>>> HOMECARD_ELEMENT_SERIALIZERS_KEY = ResourceKey.createRegistryKey(SmartHome.resource("homecard_element_serializers"));
	public static final Registry<MapCodec<? extends HomeCardElement<?>>> HOMECARD_ELEMENT_SERIALIZERS = new RegistryBuilder<>(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_SERIALIZERS_KEY).create();
	public static final DeferredRegister<MapCodec<? extends HomeCardElement<?>>> DEFERRED_HOMECARD_ELEMENT = DeferredRegister.create(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_SERIALIZERS, SmartHome.MODID);

	public static final ResourceKey<Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends HomeCardElement<?>>>> HOMECARD_ELEMENT_DISPATCHER_KEY = ResourceKey.createRegistryKey(SmartHome.resource("homecard_element_dispatcher"));
	public static final Registry<StreamCodec<? super RegistryFriendlyByteBuf, ? extends HomeCardElement<?>>> HOMECARD_ELEMENT_DISPATCHER = new RegistryBuilder<>(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_DISPATCHER_KEY).sync(true).create();
	public static final DeferredRegister<StreamCodec<? super RegistryFriendlyByteBuf, ? extends HomeCardElement<?>>> DEFERRED_HOMECARD_ELEMENT_DISPATCHER = DeferredRegister.create(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_DISPATCHER, SmartHome.MODID);

	@SubscribeEvent
	static void newRegistry(NewRegistryEvent event) {
		event.register(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_SERIALIZERS);
		event.register(HomeCardElementCodecRegistry.HOMECARD_ELEMENT_DISPATCHER);
	}

}
