package com.davenonymous.smarthome.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeCore {
	String name;
	List<HomeZone> zones;

	// internal values, not serialized
	UUID owner; // TODO: Owners! Plural.
	AABB bounds;
	VoxelShape shape;

	public HomeCore(String name) {
		this(name, new ArrayList<>());
	}

	public HomeCore(String name, List<HomeZone> zones) {
		this.name = name;
		this.zones = new ArrayList<>(zones);
		this.zones.forEach(z -> z.setHome(this));
		updateBounds();
	}

	public HomeCore(CompoundTag nbt) {
		Optional<HomeCore> decoded = CODEC.codec().parse(NbtOps.INSTANCE, nbt.get("home")).result();
		if(decoded.isPresent()) {
			HomeCore core = decoded.get();
			this.name = core.name;
			this.zones = new ArrayList<>(core.zones);
			this.zones.forEach(z -> z.setHome(this));
		} else {
			this.name = "invalid";
			this.zones = new ArrayList<>();
		}
		updateBounds();
	}

	public UUID owner() {
		return owner;
	}

	public HomeCore setOwner(UUID owner) {
		this.owner = owner;
		return this;
	}

	public CompoundTag writeToNBT(CompoundTag nbt) {
		Optional<Tag> encoded = CODEC.codec().encodeStart(NbtOps.INSTANCE, this).result();
		encoded.ifPresent(tag -> nbt.put("home", tag));
		return nbt;
	}


	public void updateBounds() {
		bounds = new AABB(0,0,0,0,0,0);
		shape = Shapes.empty();
		if(zones.isEmpty()) {
			return;
		}

		for(int zoneIndex = 0; zoneIndex < zones.size(); zoneIndex++) {
			AABB zoneBounds = zones.get(zoneIndex).bounds;
			bounds = bounds.minmax(zoneBounds);
			shape = Shapes.join(shape, Shapes.create(zoneBounds), BooleanOp.OR);
		}
	}

	public Optional<HomeZone> getZone(String zoneName) {
		return zones.stream().filter(z -> z.name().equals(zoneName)).findFirst();
	}

	public HomeCore addZone(HomeZone zone) {
		zones.removeIf(z -> z.name().equals(zone.name()));
		zone.setHome(this);
		zones.add(zone);
		updateBounds();
		return this;
	}

	public AABB bounds() {
		return bounds;
	}

	public String name() {
		return name;
	}

	public List<HomeZone> zones() {
		return zones;
	}

	public VoxelShape shape() {
		return shape;
	}

	public static final MapCodec<HomeCore> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(HomeCore::name),
			HomeZone.CODEC.codec().listOf().fieldOf("zones").forGetter(HomeCore::zones)
	).apply(instance, HomeCore::new));

	public HomeCore deleteZone(String zoneName) {
		zones.removeIf(z -> z.name().equals(zoneName));
		return this;
	}
}
