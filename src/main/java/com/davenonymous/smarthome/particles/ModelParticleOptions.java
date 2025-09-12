package com.davenonymous.smarthome.particles;

import com.davenonymous.smarthome.setup.DeferredRegistries;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModelParticleOptions implements ParticleOptions {
	ResourceLocation model;
	String variant;
	List<Axis> rotationAxis;
	Vec3 rotationOrigin;
	int lifetime;
	float alpha;

	public ModelParticleOptions(ResourceLocation modelLocation, String variant, List<Axis> rotationAxis, Vec3 rotationOrigin, int lifetime, float alpha) {
		this.model = modelLocation;
		this.variant = variant;
		this.rotationAxis = rotationAxis;
		this.rotationOrigin = rotationOrigin;
		this.lifetime = lifetime;
		this.alpha = alpha;
	}

	public int lifetime() {
		return lifetime;
	}

	public ResourceLocation modelLocation() {
		return model;
	}

	public List<Axis> rotationAxis() {
		return rotationAxis;
	}

	public Vec3 rotationOrigin() {
		return rotationOrigin;
	}

	public float alpha() {
		return alpha;
	}

	public String variant() {
		return variant;
	}

	public static final Codec<Axis> AXIS_CODEC = Codec.BYTE.xmap(b -> switch(b) {
		case 0 -> Axis.XN;
		case 1 -> Axis.XP;
		case 2 -> Axis.YN;
		case 3 -> Axis.YP;
		case 4 -> Axis.ZN;
		case 5 -> Axis.ZP;
		default -> Axis.XN;
	}, axis -> {
		if(axis == Axis.XN) return (byte) 0;
		if(axis == Axis.XP) return (byte) 1;
		if(axis == Axis.YN) return (byte) 2;
		if(axis == Axis.YP) return (byte) 3;
		if(axis == Axis.ZN) return (byte) 4;
		if(axis == Axis.ZP) return (byte) 5;
		return (byte) 0;
	});

	public static final StreamCodec<FriendlyByteBuf, Axis> AXIS_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.BYTE, axis -> {
			if(axis == Axis.XN) return (byte) 0;
			if(axis == Axis.XP) return (byte) 1;
			if(axis == Axis.YN) return (byte) 2;
			if(axis == Axis.YP) return (byte) 3;
			if(axis == Axis.ZN) return (byte) 4;
			if(axis == Axis.ZP) return (byte) 5;
			return (byte) 0;
		}, b -> switch(b) {
			case 0 -> Axis.XN;
			case 1 -> Axis.XP;
			case 2 -> Axis.YN;
			case 3 -> Axis.YP;
			case 4 -> Axis.ZN;
			case 5 -> Axis.ZP;
			default -> Axis.XN;
		}
	);

	public static final StreamCodec<FriendlyByteBuf, Vec3> VEC3_STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, Vec3::x,
		ByteBufCodecs.DOUBLE, Vec3::y,
		ByteBufCodecs.DOUBLE, Vec3::z,
		Vec3::new
	);



	public static final MapCodec<ModelParticleOptions> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("model").forGetter(ModelParticleOptions::modelLocation),
		Codec.STRING.optionalFieldOf("variant", "standalone").forGetter(ModelParticleOptions::variant),
		AXIS_CODEC.listOf().fieldOf("rotationAxis").forGetter(ModelParticleOptions::rotationAxis),
		Vec3.CODEC.fieldOf("rotationOrigin").forGetter(ModelParticleOptions::rotationOrigin),
		Codec.INT.fieldOf("lifetime").forGetter(ModelParticleOptions::lifetime),
		Codec.FLOAT.fieldOf("alpha").forGetter(ModelParticleOptions::alpha)
	).apply(instance, ModelParticleOptions::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModelParticleOptions> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, ModelParticleOptions::modelLocation,
		ByteBufCodecs.STRING_UTF8, ModelParticleOptions::variant,
		AXIS_STREAM_CODEC.apply(ByteBufCodecs.list()), ModelParticleOptions::rotationAxis,
		VEC3_STREAM_CODEC, ModelParticleOptions::rotationOrigin,
		ByteBufCodecs.INT, ModelParticleOptions::lifetime,
		ByteBufCodecs.FLOAT, ModelParticleOptions::alpha,
		ModelParticleOptions::new
	);

	@Override
	public @NotNull ParticleType<?> getType() {
		return DeferredRegistries.MODEL_PARTICLE.get();
	}
}
