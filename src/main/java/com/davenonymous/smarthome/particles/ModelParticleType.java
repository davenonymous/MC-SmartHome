package com.davenonymous.smarthome.particles;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class ModelParticleType extends ParticleType<ModelParticleOptions> {
	public ModelParticleType(boolean overrideLimiter) {
		super(overrideLimiter);
	}

	@Override
	public MapCodec<ModelParticleOptions> codec() {
		return ModelParticleOptions.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, ModelParticleOptions> streamCodec() {
		return ModelParticleOptions.STREAM_CODEC;
	}
}
