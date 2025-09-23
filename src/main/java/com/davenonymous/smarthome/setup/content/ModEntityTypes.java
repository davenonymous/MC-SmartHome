package com.davenonymous.smarthome.setup.content;

import com.davenonymous.smarthome.SmartHome;
import com.davenonymous.smarthome.entities.PlacedProjectBoxEntity;
import com.davenonymous.smarthome.entities.PlacedProjectBoxEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, SmartHome.MODID);

	public static final DeferredHolder<EntityType<?>, PlacedProjectBoxEntityType> PLACED_PROJECT_BOX_ENTITY_TYPE =
		ENTITY_TYPES.register("placed_project_box", () -> new PlacedProjectBoxEntityType(PlacedProjectBoxEntity::new, PlacedProjectBoxEntityType.DEFAULT_DIMENSIONS));
}
