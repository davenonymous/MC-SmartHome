package com.davenonymous.smarthome.entities;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.block.Block;

public class PlacedProjectBoxEntityType extends EntityType<PlacedProjectBoxEntity> {
	public PlacedProjectBoxEntityType(EntityFactory<PlacedProjectBoxEntity> factory, EntityDimensions dimensions) {
		super(factory, MobCategory.MISC, true, false, true, true,
			ImmutableSet.<Block>builder().build(), dimensions, 1.0f, 4, 10, FeatureFlagSet.of());
	}

	public static final EntityDimensions DEFAULT_DIMENSIONS = EntityDimensions
		.fixed(6/16f, 6/16f)
		.withEyeHeight(3/16f);
}
