package com.davenonymous.smarthome.entities;

import com.davenonymous.smarthome.particles.ModelParticle;
import com.davenonymous.smarthome.particles.ModelParticleOptions;
import com.davenonymous.smarthome.setup.content.ModEntityTypes;
import com.davenonymous.smarthome.setup.content.ModParticleModels;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class PlacedProjectBoxEntity extends Entity {
	public PlacedProjectBoxEntity(EntityType<?> entityType, Level level) {
		super(entityType, level);
		noPhysics = true;

	}

	public PlacedProjectBoxEntity(Level level, Vec3 position, Direction facing) {
		this(ModEntityTypes.PLACED_PROJECT_BOX_ENTITY_TYPE.get(), level);
		this.setPos(position);
		switch(facing) {
			case NORTH -> this.setYRot(180.0f);
			case SOUTH -> this.setYRot(0.0f);
			case WEST -> this.setYRot(90.0f);
			case EAST -> this.setYRot(-90.0f);
		}
	}

	@Override
	public boolean isColliding(BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	public boolean isEffectiveAi() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();

		if(!this.level().isClientSide()) {
			return;
		}

		if(!(this.level() instanceof ClientLevel level)) {
			return;
		}

		if(this.level().getGameTime() % 20 != 0) {
			return;
		}

		var model = Minecraft.getInstance().getModelManager().getModel(ModParticleModels.PROJECT_BOX);
		List<Axis> rotationAxes = switch (getDirection()) {
			case NORTH -> List.of(Axis.YP, Axis.YP);
			case SOUTH -> List.of();
			case WEST -> List.of(Axis.YN);
			case EAST -> List.of(Axis.YP);
			case UP -> List.of();
			case DOWN -> List.of();
		};
		Vec3 particleOffset = switch(getDirection()) {
			case NORTH -> new Vec3(4/16f, -1/16f, 1.5f/16f);
			case SOUTH -> new Vec3(-4/16f, -1/16f, -1.5/16f);
			case WEST -> new Vec3(1.5f/16f, -1/16f, -4/16f);
			case EAST -> new Vec3(-1.5f/16f, -1/16f, 4/16f);
			case UP -> new Vec3(0, 0, 0);
			case DOWN -> new Vec3(0, 0, 0);
		};
		var particle = new ModelParticle(level, model, this.position().add(particleOffset),
			new ModelParticleOptions(
				ModParticleModels.PROJECT_BOX.id(), "standalone",
				rotationAxes, ModParticleModels.PROJECT_BOX_AABB.getCenter(), 20, 1.0f
			)
		);
		Minecraft.getInstance().particleEngine.add(particle);
		//SmartHome.LOGGER.info("Ticking placed project box entity at " + this.position());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compoundTag) {
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compoundTag) {
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		return false;
	}

}
