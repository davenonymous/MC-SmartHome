package com.davenonymous.smarthome.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModelParticleProvider implements ParticleProvider<ModelParticleOptions> {

	public ModelParticleProvider() {
	}

	@Override
	public @Nullable Particle createParticle(@NotNull ModelParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		var bakedModel = Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(options.modelLocation()));

		return new ModelParticle(level, bakedModel, new Vec3(x, y, z), options);
	}
}
