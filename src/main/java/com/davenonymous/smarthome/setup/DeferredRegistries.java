package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.setup.content.*;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeferredRegistries {
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, SmartHome.MODID);
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SmartHome.MODID);

	public static void register(IEventBus modbus) {
		ARGUMENT_TYPES.register(modbus);
		ModBlocks.BLOCKS.register(modbus);
		ModBlocks.BLOCK_ENTITIES.register(modbus);
		ModBlocks.BLOCK_TYPES.register(modbus);
		ModCreativeTabs.CREATIVE_MODE_TABS.register(modbus);
		ModContainers.CONTAINERS.register(modbus);
		DATA_COMPONENTS.register(modbus);
		ModItems.ITEMS.register(modbus);
		ModParticles.PARTICLE_TYPES.register(modbus);
	}
}
