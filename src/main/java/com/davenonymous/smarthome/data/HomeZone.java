package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.util.MoreCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;

public class HomeZone {
	AABB bounds;
	String name;

	HomeCore home;

	public String name() {
		return name;
	}

	public AABB bounds() {
		return bounds;
	}

	public HomeZone(String name, AABB bounds) {
		this.name = name;
		this.bounds = bounds;
	}

	public HomeCore home() {
		return home;
	}

	public HomeZone setHome(HomeCore home) {
		this.home = home;
		return this;
	}

	public static final MapCodec<HomeZone> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.STRING.fieldOf("name").forGetter(HomeZone::name),
		MoreCodecs.AABB_CODEC.fieldOf("bounds").forGetter(HomeZone::bounds)
	).apply(instance, HomeZone::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeZone> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, HomeZone::name,
		MoreCodecs.AABB_STREAM_CODEC, HomeZone::bounds,
		HomeZone::new
	);

}
