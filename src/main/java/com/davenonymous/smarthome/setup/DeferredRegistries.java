package com.davenonymous.smarthome.setup;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.particles.ModelParticleType;
import com.davenonymous.smarthome.setup.content.ModParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DeferredRegistries {
	public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, SmartHome.MODID);
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SmartHome.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SmartHome.MODID);
	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, SmartHome.MODID);


	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, SmartHome.MODID);
	public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SmartHome.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmartHome.MODID);

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SmartHome.MODID);
	public static final DeferredHolder<ParticleType<?>, ModelParticleType> MODEL_PARTICLE =
		PARTICLE_TYPES.register("model_particle", () -> new ModelParticleType(false));


	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SmartHome.MODID);

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SmartHome.MODID);

	public static void register(IEventBus modbus) {
		ARGUMENT_TYPES.register(modbus);
		BLOCKS.register(modbus);
		BLOCK_ENTITIES.register(modbus);
		BLOCK_TYPES.register(modbus);
		CONTAINERS.register(modbus);
		DATA_COMPONENTS.register(modbus);
		ITEMS.register(modbus);
		PARTICLE_TYPES.register(modbus);
		RECIPE_SERIALIZERS.register(modbus);
		RECIPE_TYPES.register(modbus);

	}
}
