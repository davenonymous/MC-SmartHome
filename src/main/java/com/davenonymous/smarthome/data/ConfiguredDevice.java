package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.api.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.lib.BiggerStreamCodec;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record ConfiguredDevice(UUID id, BlockPos pos, String name, ResourceLocation blockId, boolean enabled, boolean ignored, Map<ResourceLocation, SensorSettings> sensors) {

	public ConfiguredDevice(BlockPos pos, String name, ResourceLocation blockId, boolean enabled, boolean ignored) {
		this(UUID.randomUUID(), pos, name, blockId, enabled, ignored, Map.of());
	}


	public Optional<SensorSettings> getSettings(ResourceLocation sensorId) {
		return Optional.ofNullable(sensors.get(sensorId));
	}

	public boolean genericSensorsOnly() {
		for(var sensorId : sensors.keySet()) {
			var sensor = ModSensors.getById(sensorId);
			if(sensor != null && !sensor.isGeneric()) {
				return false;
			}
		}
		return true;
	}

	public boolean matches(Block block) {
		var givenId = block.builtInRegistryHolder().getKey().location();
		return this.blockId.equals(givenId);
	}

	public boolean matches(BlockState state) {
		return matches(state.getBlock());
	}

	public static final MapCodec<ConfiguredDevice> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		UUIDUtil.CODEC.fieldOf("id").forGetter(ConfiguredDevice::id),
		BlockPos.CODEC.fieldOf("pos").forGetter(ConfiguredDevice::pos),
		Codec.STRING.fieldOf("device").forGetter(ConfiguredDevice::name),
		ResourceLocation.CODEC.fieldOf("block").forGetter(ConfiguredDevice::blockId),
		Codec.BOOL.optionalFieldOf("enabled", false).forGetter(ConfiguredDevice::enabled),
		Codec.BOOL.optionalFieldOf("ignored", false).forGetter(ConfiguredDevice::ignored),
		Codec.unboundedMap(ResourceLocation.CODEC, SensorSettings.CODEC).fieldOf("sensors").forGetter(ConfiguredDevice::sensors)
	).apply(instance, ConfiguredDevice::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ConfiguredDevice> STREAM_CODEC = BiggerStreamCodec.composite(
		UUIDUtil.STREAM_CODEC, ConfiguredDevice::id,
		BlockPos.STREAM_CODEC, ConfiguredDevice::pos,
		ByteBufCodecs.STRING_UTF8, ConfiguredDevice::name,
		ResourceLocation.STREAM_CODEC, ConfiguredDevice::blockId,
		ByteBufCodecs.BOOL, ConfiguredDevice::enabled,
		ByteBufCodecs.BOOL, ConfiguredDevice::ignored,
		ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, SensorSettings.STREAM_CODEC), ConfiguredDevice::sensors,
		ConfiguredDevice::new
	);

	public ConfiguredDevice withName(String newName) {
		return new ConfiguredDevice(this.id, this.pos, newName, this.blockId, this.enabled, this.ignored, this.sensors);
	}

	public ConfiguredDevice withEnabled(boolean newEnabled) {
		return new ConfiguredDevice(this.id, this.pos, this.name, this.blockId, newEnabled, this.ignored, this.sensors);
	}

	public ConfiguredDevice withIgnored(boolean newIgnored) {
		return new ConfiguredDevice(this.id, this.pos, this.name, this.blockId, this.enabled, newIgnored, this.sensors);
	}

	public ConfiguredDevice withSensors(Map<ResourceLocation, SensorSettings> newSensors) {
		return new ConfiguredDevice(this.id, this.pos, this.name, this.blockId, this.enabled, this.ignored, newSensors);
	}
}
