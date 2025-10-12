package com.davenonymous.smarthome.data;

import com.davenonymous.smarthome.content.sensor.sensortypes.HomeSensor;
import com.davenonymous.smarthome.content.sensor.settings.SensorSettings;
import com.davenonymous.smarthome.setup.dynamic.ModSensors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record EntityId(UUID deviceId, ResourceLocation sensorId) {
	public static final MapCodec<EntityId> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			UUIDUtil.CODEC.fieldOf("deviceId").forGetter(EntityId::deviceId),
			ResourceLocation.CODEC.fieldOf("sensorId").forGetter(EntityId::sensorId)
	).apply(instance, EntityId::new));

	public static final Codec<List<EntityId>> LIST_CODEC = Codec.list(CODEC.codec());

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityId> STREAM_CODEC = StreamCodec.composite(
			UUIDUtil.STREAM_CODEC, EntityId::deviceId,
			ResourceLocation.STREAM_CODEC, EntityId::sensorId,
			EntityId::new
	);

	public HomeSensor<?, ?> sensor() {
		return ModSensors.getById(sensorId);
	}

	public Optional<SensorSettings> getSettings(HomeCore home) {
		var optDevInfo = home.getDevice(deviceId);
		if(optDevInfo.isPresent()) {
			var devInfo = optDevInfo.get();
			ConfiguredDevice device = devInfo.getSecond();
			var optSettings = device.getSettings(sensorId);
			if(optSettings.isPresent()) {
				return optSettings;
			}
		}

		return Optional.empty();
	}
}
