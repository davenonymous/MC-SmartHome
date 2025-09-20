package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.lib.DimPos;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeCore {
	UUID id;
	UUID owner;
	String name;
	List<HomeZone> zones;
	DimPos serverLocation;

	// internal values, not serialized
	AABB bounds;
	VoxelShape shape;

	public HomeCore(String name, UUID owner, DimPos serverLocation) {
		this(UUID.randomUUID(), owner, serverLocation, name, new ArrayList<>());
	}

	public HomeCore(UUID id, UUID owner, DimPos serverLocation, String name, List<HomeZone> zones) {
		this.name = name;
		this.id = id;
		this.owner = owner;
		this.serverLocation = serverLocation;
		this.zones = new ArrayList<>(zones);
		this.zones.forEach(z -> z.setHome(this));
		updateBounds();
	}

	public HomeCore(CompoundTag nbt) {
		Optional<HomeCore> decoded = CODEC.codec().parse(NbtOps.INSTANCE, nbt.get("home")).result();
		if(decoded.isPresent()) {
			HomeCore core = decoded.get();
			this.id = core.id;
			this.name = core.name;
			this.serverLocation = core.serverLocation;
			this.zones = new ArrayList<>(core.zones);
			this.zones.forEach(z -> z.setHome(this));
		} else {
			this.name = "invalid";
			this.zones = new ArrayList<>();
		}
		updateBounds();
	}

	public boolean contains(BlockPos pos) {
		return contains(pos.getX(), pos.getY(), pos.getZ());
	}

	public boolean contains(Vec3 vec) {
		return contains(vec.x, vec.y, vec.z);
	}

	public boolean contains(double x, double y, double z) {
		return bounds.contains(x, y, z);
	}

	public HomeZone getZoneContaining(BlockPos pos) {
		return getZoneContaining(pos.getX(), pos.getY(), pos.getZ());
	}

	public HomeZone getZoneContaining(Vec3 vec) {
		return getZoneContaining(vec.x, vec.y, vec.z);
	}

	public HomeZone getZoneContaining(double x, double y, double z) {
		return zones.stream().filter(zone -> zone.bounds.contains(x, y, z)).findFirst().orElse(null);
	}

	public UUID owner() {
		return owner;
	}

	public HomeCore setOwner(UUID owner) {
		this.owner = owner;
		return this;
	}

	public HomeCore setServerLocation(DimPos serverLocation) {
		this.serverLocation = serverLocation;
		return this;
	}

	public HomeCore setName(String name) {
		this.name = name;
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

	public Optional<HomeZone> getZone(UUID zoneId) {
		return zones.stream().filter(z -> z.id().equals(zoneId)).findFirst();
	}

	public HomeCore addZone(HomeZone zone) {
		zones.removeIf(z -> z.name().equals(zone.name()));
		zone.setHome(this);
		zones.add(zone);
		updateBounds();
		return this;
	}

	public UUID id() {
		return id;
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

	public VoxelShape normalizedShape() {
		if(shape.isEmpty()) {
			return shape;
		}
		return shape.move(-shape.bounds().minX, -shape.bounds().minY, -shape.bounds().minZ);
	}

	public DimPos serverLocation() {
		return serverLocation;
	}

	public static final MapCodec<HomeCore> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			UUIDUtil.STRING_CODEC.fieldOf("id").forGetter(HomeCore::id),
			UUIDUtil.STRING_CODEC.fieldOf("owner").forGetter(HomeCore::owner),
			DimPos.CODEC.fieldOf("location").forGetter(HomeCore::serverLocation),
			Codec.STRING.fieldOf("name").forGetter(HomeCore::name),
			HomeZone.CODEC.codec().listOf().fieldOf("zones").forGetter(HomeCore::zones)
	).apply(instance, HomeCore::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeCore> STREAM_CODEC = StreamCodec.composite(
		UUIDUtil.STREAM_CODEC, HomeCore::id,
		UUIDUtil.STREAM_CODEC, HomeCore::owner,
		DimPos.STREAM_CODEC, HomeCore::serverLocation,
		ByteBufCodecs.STRING_UTF8, HomeCore::name,
		HomeZone.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeCore::zones,
		HomeCore::new
	);

	public HomeCore deleteZone(String zoneName) {
		zones.removeIf(z -> z.name().equals(zoneName));
		return this;
	}
}
