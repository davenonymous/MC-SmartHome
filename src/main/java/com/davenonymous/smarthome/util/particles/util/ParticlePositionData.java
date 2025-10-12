package com.davenonymous.smarthome.util.particles.util;

import com.davenonymous.smarthome.util.AxisDirectionHelper;
import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record ParticlePositionData(Vec3 pos, Axis direction) {

	@Override
	public @NotNull String toString() {
		return "Particle[pos=" + pos + ", direction=" + AxisDirectionHelper.directionFromAxis(direction).getName() + "]";
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ParticlePositionData(Vec3 otherPos, Axis otherDirection))) {
			return false;
		}

		if(AxisDirectionHelper.axisToByte(direction()) != AxisDirectionHelper.axisToByte(otherDirection)) {
			return false;
		}

		return Objects.equals(pos(), otherPos);
	}

	@Override
	public int hashCode() {
		return Objects.hash(pos(), AxisDirectionHelper.axisToByte(direction()));
	}

	public static final MapCodec<ParticlePositionData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Vec3.CODEC.fieldOf("pos").forGetter(ParticlePositionData::pos),
		MoreCodecs.AXIS_CODEC.fieldOf("direction").forGetter(ParticlePositionData::direction)
	).apply(instance, ParticlePositionData::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ParticlePositionData> STREAM_CODEC = StreamCodec.composite(
		MoreCodecs.VEC3_STREAM_CODEC, ParticlePositionData::pos,
		MoreCodecs.AXIS_STREAM_CODEC, ParticlePositionData::direction,
		ParticlePositionData::new
	);
}
