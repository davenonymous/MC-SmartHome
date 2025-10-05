package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import com.davenonymous.smarthome.lib.DimPos;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class HomeCore {
	UUID id;
	UUID owner;
	String name;
	List<HomeZone> zones;
	DimPos serverLocation;
	HomeSettings settings;
	List<HomeCard> cards;
	List<HomeDashboard> dashboards;

	// internal values, not serialized
	AABB bounds;
	VoxelShape shape;

	public HomeCore(String name, UUID owner, DimPos serverLocation) {
		this(UUID.randomUUID(), owner, serverLocation, name, new ArrayList<>(), new HomeSettings(), new ArrayList<>(), new ArrayList<>());
	}

	public HomeCore(UUID id, UUID owner, DimPos serverLocation, String name, List<HomeZone> zones, HomeSettings settings, List<HomeCard> cards, List<HomeDashboard> dashboards) {
		this.name = name;
		this.id = id;
		this.owner = owner;
		this.serverLocation = serverLocation;
		this.zones = new ArrayList<>(zones);
		this.zones.forEach(z -> z.setHome(this));
		this.settings = settings;
		this.cards = new ArrayList<>(cards);
		this.dashboards = new ArrayList<>(dashboards);
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
			this.settings = core.settings;
			this.cards = new ArrayList<>(core.cards);
			this.dashboards = new ArrayList<>(core.dashboards);
		} else {
			this.name = "invalid";
			this.zones = new ArrayList<>();
			this.settings = new HomeSettings();
			this.cards = new ArrayList<>();
			this.dashboards = new ArrayList<>();
		}
		updateBounds();
	}

	public Map<HomeZone, List<FoundDevice>> getAllFoundDevices() {
		Map<HomeZone, List<FoundDevice>> foundDevices = new HashMap<>();
		for(HomeZone zone : zones) {
			foundDevices.put(zone, zone.foundDevices());
		}
		return foundDevices;
	}

	public Optional<HomeCard> getCard(UUID cardId) {
		return cards.stream().filter(c -> c.id().equals(cardId)).findFirst();
	}

	public Optional<Pair<HomeZone, ConfiguredDevice>> getDevice(UUID deviceId) {
		for(HomeZone zone : zones) {
			var device = zone.getDevice(deviceId);
			if(device.isPresent()) {
				return Optional.of(Pair.of(zone, device.get()));
			}
		}
		return Optional.empty();
	}

	public Map<HomeZone, List<ConfiguredDevice>> getAllDevices() {
		Map<HomeZone, List<ConfiguredDevice>> configuredDevices = new HashMap<>();
		for(HomeZone zone : zones) {
			configuredDevices.put(zone, zone.devices());
		}
		return configuredDevices;
	}

	public Map<HomeZone, List<ConfiguredDevice>> getAllConfiguredDevices() {
		Map<HomeZone, List<ConfiguredDevice>> configuredDevices = new HashMap<>();
		for(HomeZone zone : zones) {
			configuredDevices.put(zone, zone.devices().stream().filter(Predicate.not(ConfiguredDevice::ignored)).toList());
		}
		return configuredDevices;
	}

	public Map<HomeZone, List<ConfiguredDevice>> getAllIgnoredDevices() {
		Map<HomeZone, List<ConfiguredDevice>> ignoredDevices = new HashMap<>();
		for(HomeZone zone : zones) {
			ignoredDevices.put(zone, zone.devices().stream().filter(ConfiguredDevice::ignored).toList());
		}
		return ignoredDevices;
	}

	public ServerLevel getHomeLevel(MinecraftServer server) {
		return serverLocation().getServerLevel(server);
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
		return zoneStream().filter(zone -> zone.bounds.contains(x, y, z)).findFirst().orElse(null);
	}

	public HomeZone getZoneCrossing(AABB box) {
		return zoneStream().filter(zone -> zone.bounds.intersects(box)).findFirst().orElse(null);
	}

	private Stream<HomeZone> zoneStream() {
		return this.zoneStream(false);
	}

	private Stream<HomeZone> zoneStream(boolean includeDeleted) {
		return zones.stream().filter(zone -> includeDeleted || !zone.isDeleted());
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

	public void setFoundDevices(Map<HomeZone, List<FoundDevice>> foundDevices) {
		for(var entry : foundDevices.entrySet()) {
			var zone = entry.getKey();
			var devices = entry.getValue();
			zone.foundDevices().clear();
			zone.foundDevices().addAll(devices);
		}
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

		for(HomeZone zone : zones) {
			if(zone.isDeleted()) {
				continue;
			}

			AABB zoneBounds = zone.bounds;
			bounds = bounds.minmax(zoneBounds);
			shape = Shapes.join(shape, Shapes.create(zoneBounds), BooleanOp.OR);
		}
	}

	public Optional<HomeZone> getZone(String zoneName) {
		return zoneStream().filter(z -> z.name().equals(zoneName)).findFirst();
	}

	public Optional<HomeZone> getZone(UUID zoneId) {
		return zoneStream().filter(z -> z.id().equals(zoneId)).findFirst();
	}

	public HomeCore addZone(HomeZone zone) {
		zones.removeIf(z -> z.id().equals(zone.id()));
		zone.setHome(this);
		zones.add(zone);
		updateBounds();
		return this;
	}

	public HomeCore addCard(HomeCard card) {
		cards.removeIf(c -> c.id().equals(card.id()));
		cards.add(card);
		return this;
	}

	public void setSettings(HomeSettings settings) {
		this.settings = settings;
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

	public HomeSettings settings() {
		return settings;
	}

	public List<HomeCard> cards() {
		return cards;
	}

	public List<HomeDashboard> dashboards() {
		return dashboards;
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
			HomeZone.CODEC.codec().listOf().fieldOf("zones").forGetter(HomeCore::zones),
			HomeSettings.CODEC.codec().optionalFieldOf("settings", new HomeSettings()).forGetter(HomeCore::settings),
			HomeCard.CODEC.codec().listOf().optionalFieldOf("cards", new ArrayList<>()).forGetter(HomeCore::cards),
			HomeDashboard.CODEC.codec().listOf().optionalFieldOf("dashboards", new ArrayList<>()).forGetter(HomeCore::dashboards)
	).apply(instance, HomeCore::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, HomeCore> STREAM_CODEC = BiggerStreamCodec.composite(
		UUIDUtil.STREAM_CODEC, HomeCore::id,
		UUIDUtil.STREAM_CODEC, HomeCore::owner,
		DimPos.STREAM_CODEC, HomeCore::serverLocation,
		ByteBufCodecs.STRING_UTF8, HomeCore::name,
		HomeZone.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeCore::zones,
		HomeSettings.STREAM_CODEC, HomeCore::settings,
		HomeCard.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeCore::cards,
		HomeDashboard.STREAM_CODEC.apply(ByteBufCodecs.list()), HomeCore::dashboards,
		HomeCore::new
	);

	public HomeCore deleteZone(String zoneName) {
		zones.removeIf(z -> z.name().equals(zoneName));
		return this;
	}
}
