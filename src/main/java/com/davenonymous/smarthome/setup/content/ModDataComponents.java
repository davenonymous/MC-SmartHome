package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.items.ServerDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SmartHome.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<ServerDataComponent>> SERVER_DATA_COMPONENT = DATA_COMPONENTS.registerComponentType(
		"server",
		builder -> builder
			.persistent(ServerDataComponent.CODEC.codec())
			.networkSynchronized(ServerDataComponent.STREAM_CODEC)
	);
}
